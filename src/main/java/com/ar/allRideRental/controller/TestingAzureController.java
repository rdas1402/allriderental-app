package com.ar.allRideRental.controller;

import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.service.VehicleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api")
//@CrossOrigin(origins = "http://localhost:3000")
public class TestingAzureController {
    
    @GetMapping("/message")
    public String message() {
        return "Congrats! Your app deployed successfully in Azure!!";
    }
}