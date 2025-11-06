-- =============================================
-- Database Schema for All Ride Rental
-- MySQL Compatible Version
-- =============================================

-- Drop tables if they exist (for clean setup)
DROP TABLE IF EXISTS bookings;
DROP TABLE IF EXISTS vehicle_features;
DROP TABLE IF EXISTS vehicle_availability;
DROP TABLE IF EXISTS vehicles;
DROP TABLE IF EXISTS users;
DROP TABLE IF EXISTS cities;

-- Create cities table
CREATE TABLE cities (
                        id BIGINT AUTO_INCREMENT PRIMARY KEY,
                        name VARCHAR(255) NOT NULL UNIQUE,
                        state VARCHAR(255) NOT NULL,
                        is_active BOOLEAN DEFAULT true,
                        created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Create vehicles table
CREATE TABLE vehicles (
                          id BIGINT AUTO_INCREMENT PRIMARY KEY,
                          name VARCHAR(255) NOT NULL,
                          type VARCHAR(50) NOT NULL,
                          price VARCHAR(100) NOT NULL,
                          rating DECIMAL(3,2) DEFAULT 0.0,
                          city VARCHAR(255) NOT NULL,
                          image_url VARCHAR(500),
                          is_available BOOLEAN DEFAULT true,
                          under_maintenance BOOLEAN DEFAULT false,
                          status VARCHAR(50) DEFAULT 'available',
                          capacity INTEGER,
                          description VARCHAR(255),
                          fuel_type VARCHAR(255),
                          transmission VARCHAR(255),
                          created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                          updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create vehicle_features table
CREATE TABLE vehicle_features (
                                  id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                  vehicle_id BIGINT NOT NULL,
                                  feature VARCHAR(255) NOT NULL,
                                  FOREIGN KEY (vehicle_id) REFERENCES vehicles(id) ON DELETE CASCADE
);

-- Create users table
CREATE TABLE users (
                       id BIGINT AUTO_INCREMENT PRIMARY KEY,
                       phone VARCHAR(15) NOT NULL UNIQUE,
                       name VARCHAR(255) NOT NULL,
                       email VARCHAR(255),
                       dob DATE,
                       role VARCHAR(50) DEFAULT 'user',
                       is_admin BOOLEAN DEFAULT false,
                       is_active BOOLEAN DEFAULT true,
                       created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                       updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- Create bookings table
CREATE TABLE bookings (
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
                          updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                          FOREIGN KEY (vehicle_id) REFERENCES vehicles(id)
);

-- Create vehicle_availability table
CREATE TABLE vehicle_availability (
                                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                                      vehicle_id VARCHAR(255) NOT NULL,
                                      start_date DATE NOT NULL,
                                      end_date DATE NOT NULL,
                                      is_available BOOLEAN DEFAULT true,
                                      reason TEXT,
                                      created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
                                      updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- =============================================
-- Create Indexes for Better Performance
-- =============================================

-- Cities indexes
CREATE INDEX idx_city_name ON cities(name);

-- Vehicles indexes
CREATE INDEX idx_vehicle_type ON vehicles(type);
CREATE INDEX idx_vehicle_city ON vehicles(city);
CREATE INDEX idx_vehicle_rating ON vehicles(rating);

-- Vehicle features index
CREATE INDEX idx_vehicle_features ON vehicle_features(vehicle_id);

-- Bookings indexes
CREATE INDEX idx_customer_phone ON bookings(customer_phone);
CREATE INDEX idx_vehicle_id ON bookings(vehicle_id);
CREATE INDEX idx_status ON bookings(status);
CREATE INDEX idx_dates ON bookings(start_date, end_date);
CREATE INDEX idx_booking_date ON bookings(booking_date);
CREATE INDEX idx_created_at ON bookings(created_at);

-- Users index
CREATE INDEX idx_user_phone ON users(phone);

-- Vehicle availability indexes
CREATE INDEX idx_availability_vehicle ON vehicle_availability(vehicle_id);
CREATE INDEX idx_availability_dates ON vehicle_availability(start_date, end_date);