package com.ar.allRideRental.controller;

import com.ar.allRideRental.service.UserService;
import com.ar.allRideRental.service.OTPService;
import com.ar.allRideRental.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private OTPService otpService;

    // Inner class to store OTP with timestamp
    private static class OTPData {
        String otp;
        LocalDateTime createdAt;
        int attemptCount;
        boolean sentToMobile;

        OTPData(String otp) {
            this.otp = otp;
            this.createdAt = LocalDateTime.now();
            this.attemptCount = 0;
            this.sentToMobile = false;
        }
    }

    // In-memory storage for OTPs with timestamp
    private final Map<String, OTPData> otpStorage = new ConcurrentHashMap<>();

    @PostMapping("/send-otp")
    public ResponseEntity<?> sendOtp(@RequestBody Map<String, String> request) {
        // Clean up expired OTPs first
        cleanupExpiredOtps();

        // Try both possible field names
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null) {
            phoneNumber = request.get("phone");
        }

        // Validate phone number
        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Phone number is required"
            ));
        }

        // Validate phone number format (Indian numbers: 10 digits starting with 6-9)
        if (!phoneNumber.matches("^[6-9]\\d{9}$")) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Please enter a valid 10-digit phone number starting with 6-9"
            ));
        }

        try {
            // Check if OTP was recently sent
            OTPData existingOtp = otpStorage.get(phoneNumber);
            if (existingOtp != null) {
                long secondsSinceLastOtp = ChronoUnit.SECONDS.between(existingOtp.createdAt, LocalDateTime.now());
                if (secondsSinceLastOtp < 30) {
                    return ResponseEntity.badRequest().body(Map.of(
                            "success", false,
                            "message", "Please wait " + (30 - secondsSinceLastOtp) + " seconds before requesting new OTP"
                    ));
                }
            }

            // Generate OTP
            String otp = otpService.generateOTP();

            // Store OTP with timestamp
            OTPData otpData = new OTPData(otp);
            otpStorage.put(phoneNumber, otpData);

            // Send OTP via SMS
            try {
                String smsResult = otpService.sendOTP(phoneNumber, otp).block();
                otpData.sentToMobile = true;

                System.out.println("=== OTP SMS ATTEMPT COMPLETED ===");
                System.out.println("To: +91 " + phoneNumber);
                System.out.println("OTP: " + otp);
                System.out.println("SMS Result: " + smsResult);
                System.out.println("Timestamp: " + LocalDateTime.now());
                System.out.println("=== END OTP SMS ===");

                // Parse the response to check if SMS was actually sent
                if (smsResult != null && smsResult.contains("success")) {
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "OTP sent successfully to your mobile number"
                    ));
                } else {
                    // SMS failed but OTP was generated - show in console for development
                    System.out.println("⚠️ SMS may not have been delivered. OTP for development: " + otp);
                    return ResponseEntity.ok(Map.of(
                            "success", true,
                            "message", "OTP generated. Check console for OTP: " + otp
                    ));
                }

            } catch (Exception smsError) {
                System.err.println("SMS sending failed: " + smsError.getMessage());

                // Fallback: Show OTP in console for development
                System.out.println("=== OTP FALLBACK (SMS Failed) ===");
                System.out.println("Phone: +91 " + phoneNumber);
                System.out.println("OTP: " + otp);
                System.out.println("SMS Error: " + smsError.getMessage());
                System.out.println("=== END OTP FALLBACK ===");

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP generated. Check console for OTP: " + otp
                ));
            }

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to send OTP: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody Map<String, String> request) {
        // Clean up expired OTPs first
        cleanupExpiredOtps();

        // Try both possible field names
        String phoneNumber = request.get("phoneNumber");
        if (phoneNumber == null) {
            phoneNumber = request.get("phone");
        }

        String otp = request.get("otp");

        if (phoneNumber == null || phoneNumber.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Phone number is required"
            ));
        }

        if (otp == null || otp.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "OTP is required"
            ));
        }

        try {
            OTPData otpData = otpStorage.get(phoneNumber);

            if (otpData == null) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "OTP expired or not found. Please request a new OTP."
                ));
            }

            // Check if OTP is expired (5 minutes)
            long minutesSinceCreation = ChronoUnit.MINUTES.between(otpData.createdAt, LocalDateTime.now());
            if (minutesSinceCreation > 5) {
                otpStorage.remove(phoneNumber);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "OTP has expired. Please request a new OTP."
                ));
            }

            // Increment attempt count
            otpData.attemptCount++;

            // Check if too many attempts
            if (otpData.attemptCount > 5) {
                otpStorage.remove(phoneNumber);
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Too many failed attempts. Please request a new OTP."
                ));
            }

            if (otpData.otp.equals(otp)) {
                // Remove OTP after successful verification
                otpStorage.remove(phoneNumber);

                System.out.println("=== OTP VERIFIED SUCCESSFULLY ===");
                System.out.println("Phone: +91 " + phoneNumber);
                System.out.println("Timestamp: " + LocalDateTime.now());
                System.out.println("=== END OTP VERIFICATION ===");

                return ResponseEntity.ok(Map.of(
                        "success", true,
                        "message", "OTP verified successfully"
                ));
            } else {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Invalid OTP. Attempts remaining: " + (5 - otpData.attemptCount)
                ));
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "OTP verification failed: " + e.getMessage()
            ));
        }
    }

    private void cleanupExpiredOtps() {
        LocalDateTime now = LocalDateTime.now();
        otpStorage.entrySet().removeIf(entry -> {
            long minutesSinceCreation = ChronoUnit.MINUTES.between(entry.getValue().createdAt, now);
            return minutesSinceCreation > 5; // Remove OTPs older than 5 minutes
        });
    }

    // ... rest of your existing methods (checkUserExists, createUser, etc.) remain the same
    @GetMapping("/check-user/{phoneNumber}")
    public ResponseEntity<?> checkUserExists(@PathVariable String phoneNumber) {
        try {
            boolean exists = userService.checkUserExists(phoneNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);

            if (exists) {
                Optional<User> userOptional = userService.getUserByPhone(phoneNumber);
                response.put("user", userOptional.orElse(null));
            } else {
                response.put("user", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to check user: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/users/check/{phoneNumber}")
    public ResponseEntity<?> checkUserExistsAlternative(@PathVariable String phoneNumber) {
        try {
            boolean exists = userService.checkUserExists(phoneNumber);

            Map<String, Object> response = new HashMap<>();
            response.put("exists", exists);

            if (exists) {
                Optional<User> userOptional = userService.getUserByPhone(phoneNumber);
                response.put("user", userOptional.orElse(null));
            } else {
                response.put("user", null);
            }

            return ResponseEntity.ok(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to check user: " + e.getMessage()
            ));
        }
    }

    @PostMapping("/create-user")
    public ResponseEntity<?> createUser(@RequestBody Map<String, Object> userData) {
        try {
            String phone = (String) userData.get("phone");
            String name = (String) userData.get("name");
            String email = (String) userData.get("email");
            String dob = (String) userData.get("dob");

            // Validate required fields
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phone number is required"
                ));
            }

            if (name == null || name.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Name is required"
                ));
            }

            // Check if user already exists
            if (userService.checkUserExists(phone)) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "User with this phone number already exists"
                ));
            }

            var user = userService.createUser(phone, name, email, java.time.LocalDate.parse(dob));
            return ResponseEntity.ok(user);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "error", "Failed to create user: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/profile/{phoneNumber}")
    public ResponseEntity<?> getUserProfile(@PathVariable String phoneNumber) {
        try {
            var user = userService.getUserByPhone(phoneNumber);
            if (user.isPresent()) {
                return ResponseEntity.ok(user.get());
            } else {
                return ResponseEntity.notFound().build();
            }
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to get user profile: " + e.getMessage()
            ));
        }
    }

    @PutMapping("/update-profile")
    public ResponseEntity<?> updateProfile(@RequestBody Map<String, Object> userData) {
        try {
            String phone = (String) userData.get("phone");
            String name = (String) userData.get("name");
            String email = (String) userData.get("email");
            String dob = (String) userData.get("dob");

            // Validate required fields
            if (phone == null || phone.trim().isEmpty()) {
                return ResponseEntity.badRequest().body(Map.of(
                        "success", false,
                        "message", "Phone number is required"
                ));
            }

            User updatedUser = userService.updateUserProfile(phone, name, email, java.time.LocalDate.parse(dob));

            return ResponseEntity.ok(Map.of(
                    "success", true,
                    "message", "Profile updated successfully",
                    "user", updatedUser
            ));
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().body(Map.of(
                    "success", false,
                    "message", "Failed to update profile: " + e.getMessage()
            ));
        }
    }

    @GetMapping("/bookings/{phoneNumber}")
    public ResponseEntity<?> getUserBookings(@PathVariable String phoneNumber) {
        try {
            var bookings = userService.getUserBookings(phoneNumber);
            return ResponseEntity.ok(bookings);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                    "error", "Failed to get user bookings: " + e.getMessage()
            ));
        }
    }
}