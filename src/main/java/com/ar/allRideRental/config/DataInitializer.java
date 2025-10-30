package com.ar.allRideRental.config;

import com.ar.allRideRental.model.City;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.CityRepository;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {
    
    @Autowired
    private CityRepository cityRepository;
    
    @Autowired
    private VehicleRepository vehicleRepository;
    
    @Override
    public void run(String... args) throws Exception {
        // Initialize Cities
        if (cityRepository.count() == 0) {
            List<City> cities = Arrays.asList(
                new City("Guwahati", "Assam"),
                new City("Jorhat", "Assam"),
                new City("Sivsagar", "Assam"),
                new City("Dibrugarh", "Assam"),
                new City("Tinsukia", "Assam")
            );
            cityRepository.saveAll(cities);
            System.out.println("✅ Cities initialized: " + cities.size());
        }

        // Initialize Vehicles
        if (vehicleRepository.count() == 0) {
            List<Vehicle> vehicles = Arrays.asList(
                // Premium Cars
                new Vehicle("Mercedes-Benz E-Class", "Car", "₹4,500/day",
                    Arrays.asList("Luxury Sedan", "Premium Sound", "Panoramic Roof"), 4.8, "Guwahati",
                    "https://images.unsplash.com/photo-1563720223485-8d84e6af6c7a?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),

                new Vehicle("BMW X5", "Car", "₹5,200/day",
                    Arrays.asList("Luxury SUV", "4WD", "Heated Seats"), 4.7, "Sivsagar",
                    "https://images.unsplash.com/photo-1555215695-3004980ad54e?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),

                new Vehicle("Audi A6", "Car", "₹3,800/day",
                    Arrays.asList("Executive Sedan", "Virtual Cockpit", "Matrix LED"), 4.6, "Jorhat",
                    "https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),
                
                new Vehicle("Range Rover Velar", "Car", "₹6,500/day", 
                    Arrays.asList("Luxury SUV", "Terrain Response", "Meridian Sound"), 4.7, "Tinsukia",
                    "https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),
                
                // Adventure Bikes
                new Vehicle("Ducati Panigale V4", "Bike", "₹2,800/day", 
                    Arrays.asList("Superbike", "1103cc", "Race Mode"), 4.9, "Guwahati",
                    "https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),
                
                new Vehicle("Harley Davidson Street Glide", "Bike", "₹3,200/day", 
                    Arrays.asList("Cruiser", "1868cc", "Tour Package"), 4.7, "Sivsagar",
                    "https://images.unsplash.com/photo-1571702976730-d9fd4ce8452a?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),
                
                new Vehicle("KTM 390 Duke", "Bike", "₹1,500/day", 
                    Arrays.asList("Naked Bike", "373cc", "ABS Pro"), 4.5, "Jorhat",
                    "https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80"),
                
                new Vehicle("Triumph Tiger 900", "Bike", "₹2,200/day", 
                    Arrays.asList("Adventure", "888cc", "Off-road Pack"), 4.8, "Tinsukia",
                    "https://images.unsplash.com/photo-1571068316344-75bc76f77890?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80")
            );
            
            vehicleRepository.saveAll(vehicles);
            System.out.println("✅ Vehicles initialized: " + vehicles.size());
        }
    }
}