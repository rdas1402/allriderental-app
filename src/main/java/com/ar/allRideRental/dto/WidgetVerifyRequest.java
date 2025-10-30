package com.ar.allRideRental.dto;

public class WidgetVerifyRequest {
    private String accessToken;
    
    public WidgetVerifyRequest() {}
    
    public WidgetVerifyRequest(String accessToken) {
        this.accessToken = accessToken;
    }
    
    public String getAccessToken() {
        return accessToken;
    }
    
    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }
}