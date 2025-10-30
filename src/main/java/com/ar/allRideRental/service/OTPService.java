package com.ar.allRideRental.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;

@Service
public class OTPService {

    @Value("${sms.provider:msg91_whatsapp}")
    private String smsProvider;

    // MSG91 Configuration
    @Value("${msg91.authkey:}")
    private String msg91AuthKey;

    @Value("${msg91.whatsapp.template.id:}")
    private String msg91WhatsappTemplateId;

    @Value("${msg91.whatsapp.sender:919999999999}")
    private String msg91WhatsappSender;

    @Value("${app.otp.length:6}")
    private int otpLength;

    private final WebClient webClient;
    private final Random random = new Random();
    private final ObjectMapper objectMapper;

    public OTPService(WebClient.Builder webClientBuilder, ObjectMapper objectMapper) {
        this.webClient = webClientBuilder.build();
        this.objectMapper = objectMapper;
    }

    public String generateOTP() {
        int min = (int) Math.pow(10, otpLength - 1);
        int max = (int) Math.pow(10, otpLength) - 1;
        return String.valueOf(min + random.nextInt(max - min + 1));
    }

    public Mono<String> sendOTP(String phoneNumber, String otp) {
        System.out.println("=== ATTEMPTING TO SEND WHATSAPP OTP ===");
        System.out.println("Phone: " + phoneNumber);
        System.out.println("OTP: " + otp);
        System.out.println("SMS Provider: " + smsProvider);

        String formattedNumber = formatPhoneNumber(phoneNumber);

        try {
            if ("msg91_whatsapp".equals(smsProvider)) {
                return sendViaMsg91Whatsapp(formattedNumber, otp);
            } else {
                return sendViaMsg91Whatsapp(formattedNumber, otp); // Default to WhatsApp
            }
        } catch (Exception e) {
            System.err.println("OTP sending failed: " + e.getMessage());
            return Mono.error(new RuntimeException("Failed to send OTP: " + e.getMessage()));
        }
    }

    private Mono<String> sendViaMsg91Whatsapp(String phoneNumber, String otp) {
        try {
            // Remove +91 for MSG91 API
            String mobileNumber = phoneNumber.replace("+91", "");

            // Create request body for MSG91 WhatsApp
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("template_id", msg91WhatsappTemplateId);
            requestBody.put("sender", msg91WhatsappSender);
            requestBody.put("short_url", "0"); // Set to 1 if you want short URL
            requestBody.put("mobile", "91" + mobileNumber);

            // Create parameters for WhatsApp template
            Map<String, String> parameters = new HashMap<>();
            parameters.put("1", otp); // First parameter in template
            parameters.put("2", "5"); // Second parameter - validity in minutes

            requestBody.put("parameters", parameters);

            System.out.println("Sending WhatsApp OTP via MSG91 API...");
            System.out.println("Request URL: https://api.msg91.com/api/v5/whatsapp");
            System.out.println("Request Body: " + requestBody);

            return webClient.post()
                    .uri("https://api.msg91.com/api/v5/whatsapp")
                    .header("authkey", msg91AuthKey)
                    .header("Content-Type", "application/json")
                    .bodyValue(requestBody)
                    .retrieve()
                    .bodyToMono(String.class)
                    .doOnSuccess(response -> {
                        System.out.println("=== MSG91 WHATSAPP API RESPONSE ===");
                        System.out.println("Response: " + response);
                        System.out.println("=== END RESPONSE ===");

                        try {
                            JsonNode jsonResponse = objectMapper.readTree(response);
                            String type = jsonResponse.path("type").asText();
                            String message = jsonResponse.path("message").asText();

                            if ("success".equals(type)) {
                                System.out.println("✅ WhatsApp OTP sent successfully via MSG91");
                            } else {
                                System.err.println("❌ MSG91 WhatsApp API Error: " + message);
                            }
                        } catch (Exception e) {
                            System.err.println("Error parsing MSG91 response: " + e.getMessage());
                        }
                    })
                    .doOnError(error -> {
                        System.err.println("❌ MSG91 WhatsApp API Call Failed: " + error.getMessage());
                    });

        } catch (Exception e) {
            System.err.println("Exception in MSG91 WhatsApp: " + e.getMessage());
            return Mono.error(new RuntimeException("Failed to send WhatsApp OTP: " + e.getMessage()));
        }
    }

    private String formatPhoneNumber(String phoneNumber) {
        // Ensure Indian format +91XXXXXXXXXX
        String cleaned = phoneNumber.replaceAll("[^0-9]", "");
        if (cleaned.length() == 10) {
            return "+91" + cleaned;
        } else if (cleaned.startsWith("91") && cleaned.length() == 12) {
            return "+" + cleaned;
        } else if (cleaned.startsWith("+91") && cleaned.length() == 13) {
            return cleaned;
        }
        throw new RuntimeException("Invalid phone number format: " + phoneNumber);
    }

    public Mono<String> verifyOTP(String phoneNumber, String otp) {
        // For WhatsApp, we handle verification in AuthController (same as before)
        return Mono.just("OTP verification completed");
    }
}