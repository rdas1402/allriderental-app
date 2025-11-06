package com.ar.allRideRental.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer_delete implements CommandLineRunner {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        System.out.println("🔄 Checking database initialization...");

        // Verify data was loaded
        verifyData();

        System.out.println("✅ Database initialization completed!");
    }

    private void verifyData() {
        try {
            Integer cityCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM cities", Integer.class);
            Integer vehicleCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehicles", Integer.class);
            Integer featureCount = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM vehicle_features", Integer.class);

            System.out.println("📊 Database Stats:");
            System.out.println("   Cities: " + cityCount);
            System.out.println("   Vehicles: " + vehicleCount);
            System.out.println("   Features: " + featureCount);

        } catch (Exception e) {
            System.err.println("❌ Error verifying data: " + e.getMessage());
        }
    }
}