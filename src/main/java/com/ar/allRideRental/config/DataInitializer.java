package com.ar.allRideRental.config;

import com.ar.allRideRental.model.City;
import com.ar.allRideRental.model.Vehicle;
import com.ar.allRideRental.repository.CityRepository;
import com.ar.allRideRental.repository.VehicleRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class DataInitializer implements CommandLineRunner {

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private VehicleRepository vehicleRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Override
    public void run(String... args) throws Exception {
        // Create tables if they don't exist
        createTables();

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

    private void createTables() {
        try {
            System.out.println("🔄 Creating database tables...");

            // Create cities table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS cities (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL UNIQUE,
                    state VARCHAR(255) NOT NULL,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✅ Cities table created/verified");

            // Create vehicles table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS vehicles (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    name VARCHAR(255) NOT NULL,
                    type VARCHAR(50) NOT NULL,
                    price VARCHAR(100) NOT NULL,
                    rating DECIMAL(3,2) DEFAULT 0.0,
                    city VARCHAR(255) NOT NULL,
                    image_url VARCHAR(500),
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✅ Vehicles table created/verified");

            // Create vehicle_features table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS vehicle_features (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    vehicle_id BIGINT NOT NULL,
                    feature VARCHAR(255) NOT NULL,
                    FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
                )
            """);
            System.out.println("✅ Vehicle features table created/verified");

            // Create bookings table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS bookings (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    vehicle_id BIGINT NOT NULL,
                    vehicle_name VARCHAR(255) NOT NULL,
                    customer_phone VARCHAR(15) NOT NULL,
                    customer_name VARCHAR(255) NOT NULL,
                    customer_email VARCHAR(255),
                    start_date DATE NOT NULL,
                    end_date DATE NOT NULL,
                    pickup_time TIME,
                    dropoff_time TIME,
                    pickup_location VARCHAR(500),
                    additional_driver BOOLEAN DEFAULT FALSE,
                    insurance_type VARCHAR(50) DEFAULT 'basic',
                    total_amount DECIMAL(10,2) NOT NULL,
                    status VARCHAR(50) DEFAULT 'confirmed',
                    booking_date DATETIME NOT NULL,
                    created_at DATETIME NOT NULL DEFAULT CURRENT_TIMESTAMP,
                    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✅ Bookings table created/verified");

            // Create users table
            jdbcTemplate.execute("""
                CREATE TABLE IF NOT EXISTS users (
                    id BIGINT AUTO_INCREMENT PRIMARY KEY,
                    phone VARCHAR(15) NOT NULL UNIQUE,
                    name VARCHAR(255) NOT NULL,
                    email VARCHAR(255),
                    dob DATE,
                    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
                )
            """);
            System.out.println("✅ Users table created/verified");

            // Create indexes for better performance
            createIndexes();

            System.out.println("🎉 All database tables created successfully!");

        } catch (Exception e) {
            System.err.println("❌ Error creating tables: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createIndexes() {
        try {
            // Cities indexes
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_city_name ON cities(name)");

            // Vehicles indexes
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vehicle_type ON vehicles(type)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vehicle_city ON vehicles(city)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vehicle_rating ON vehicles(rating)");

            // Vehicle features index
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vehicle_features ON vehicle_features(vehicle_id)");

            // Bookings indexes
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_customer_phone ON bookings(customer_phone)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_vehicle_id ON bookings(vehicle_id)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_status ON bookings(status)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_dates ON bookings(start_date, end_date)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_booking_date ON bookings(booking_date)");
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_created_at ON bookings(created_at)");

            // Users index
            jdbcTemplate.execute("CREATE INDEX IF NOT EXISTS idx_user_phone ON users(phone)");

            System.out.println("✅ All indexes created/verified");

        } catch (Exception e) {
            System.err.println("❌ Error creating indexes: " + e.getMessage());
        }
    }
}