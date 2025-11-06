-- =============================================
-- Initial Data for All Ride Rental
-- =============================================

-- Insert Cities
INSERT INTO cities (name, state) VALUES
                                     ('Guwahati', 'Assam'),
                                     ('Jorhat', 'Assam'),
                                     ('Sivsagar', 'Assam'),
                                     ('Dibrugarh', 'Assam'),
                                     ('Tinsukia', 'Assam');

-- Insert Vehicles
INSERT INTO vehicles (name, type, price, rating, city, image_url, is_available) VALUES
                                                                                    ('Mercedes-Benz E-Class', 'Car', '₹4,500/day', 4.8, 'Guwahati', 'https://images.unsplash.com/photo-1563720223485-8d84e6af6c7a?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('BMW X5', 'Car', '₹5,200/day', 4.7, 'Sivsagar', 'https://images.unsplash.com/photo-1555215695-3004980ad54e?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('Audi A6', 'Car', '₹3,800/day', 4.6, 'Jorhat', 'https://images.unsplash.com/photo-1606664515524-ed2f786a0bd6?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('Range Rover Velar', 'Car', '₹6,500/day', 4.7, 'Tinsukia', 'https://images.unsplash.com/photo-1621007947382-bb3c3994e3fb?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('Ducati Panigale V4', 'Bike', '₹2,800/day', 4.9, 'Guwahati', 'https://paultan.org/image/2019/06/2019-Ducati-Panigale-V4-R-11.jpg', true),
                                                                                    ('Harley Davidson Street Glide', 'Bike', '₹3,200/day', 4.7, 'Sivsagar', 'https://images.unsplash.com/photo-1571702976730-d9fd4ce8452a?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('KTM 390 Duke', 'Bike', '₹1,500/day', 4.5, 'Jorhat', 'https://images.unsplash.com/photo-1558618047-3c8c76ca7d13?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true),
                                                                                    ('Triumph Tiger 900', 'Bike', '₹2,200/day', 4.8, 'Tinsukia', 'https://images.unsplash.com/photo-1571068316344-75bc76f77890?ixlib=rb-4.0.3&auto=format&fit=crop&w=500&q=80', true);

-- Insert Vehicle Features
INSERT INTO vehicle_features (vehicle_id, feature) VALUES
                                                       (1, 'Luxury Sedan'), (1, 'Premium Sound'), (1, 'Panoramic Roof'),
                                                       (2, 'Luxury SUV'), (2, '4WD'), (2, 'Heated Seats'),
                                                       (3, 'Executive Sedan'), (3, 'Virtual Cockpit'), (3, 'Matrix LED'),
                                                       (4, 'Luxury SUV'), (4, 'Terrain Response'), (4, 'Meridian Sound'),
                                                       (5, 'Superbike'), (5, '1103cc'), (5, 'Race Mode'),
                                                       (6, 'Cruiser'), (6, '1868cc'), (6, 'Tour Package'),
                                                       (7, 'Naked Bike'), (7, '373cc'), (7, 'ABS Pro'),
                                                       (8, 'Adventure'), (8, '888cc'), (8, 'Off-road Pack');

-- Insert Users (including admin users)
INSERT INTO users (phone, name, email, role, is_admin, is_active) VALUES
                                                                      ('9876543210', 'Admin User', 'admin@allriderental.com', 'admin', true, true),
                                                                      ('9123456789', 'Super Admin', 'superadmin@allriderental.com', 'admin', true, true),
                                                                      ('9876512345', 'Rupam Das', 'rupam@example.com', 'user', false, true),
                                                                      ('9123487654', 'Nishita Dutta', 'nishita@example.com', 'user', false, true);

-- Insert Sample Bookings
INSERT INTO bookings (vehicle_id, vehicle_name, customer_phone, customer_name, customer_email, start_date, end_date, pickup_time, total_amount, status, booking_date) VALUES
                                                                                                                                                                          (1, 'Mercedes-Benz E-Class', '9876512345', 'Rupam Das', 'rupam@example.com', '2025-11-10', '2025-11-12', '10:00:00', 13500.00, 'confirmed', '2025-11-04 10:00:00'),
                                                                                                                                                                          (5, 'Ducati Panigale V4', '9123487654', 'Nishita Dutta', 'nishita@example.com', '2025-11-15', '2025-11-16', '09:00:00', 2800.00, 'confirmed', '2025-11-04 11:30:00');
-- Insert Vehicle Availability
INSERT INTO vehicle_availability (vehicle_id, start_date, end_date, is_available, reason) VALUES
                                                                                              ('1', '2025-11-10', '2025-11-12', false, 'Booked by Rupam Das'),
                                                                                              ('5', '2025-11-15', '2025-11-16', false, 'Booked by Nishita Dutta'),
                                                                                              ('2', '2025-11-22', '2025-11-23', false, 'Maintenance');
-- Add purpose column with default value 'rent'
ALTER TABLE vehicles ADD COLUMN purpose VARCHAR(10) DEFAULT 'rent' NOT NULL;

-- Update existing vehicles (all existing will be for rent only)
UPDATE vehicles SET purpose = 'rent' WHERE purpose IS NULL;

-- Add check constraint for valid purposes (optional)
ALTER TABLE vehicles ADD CONSTRAINT chk_purpose CHECK (purpose IN ('rent', 'sale', 'both'));

-- Example: Set some vehicles for sale only
UPDATE vehicles SET purpose = 'sale' WHERE id IN (1, 3, 5);

-- Example: Set some vehicles for both rent and sale
UPDATE vehicles SET purpose = 'both' WHERE id IN (2, 4, 6);

-- Add new price columns
ALTER TABLE vehicles ADD COLUMN rent_price VARCHAR(50);
ALTER TABLE vehicles ADD COLUMN sale_price VARCHAR(50);

-- Migrate existing price data to rent_price
UPDATE vehicles SET rent_price = price WHERE rent_price IS NULL;

-- Set default sale prices for existing vehicles
UPDATE vehicles SET sale_price =
                        CASE
                            WHEN type = 'Car' THEN '₹8,50,000'
                            WHEN type = 'Bike' THEN '₹1,20,000'
                            ELSE '₹0'
                            END
WHERE sale_price IS NULL;

-- Set some vehicles for sale
UPDATE vehicles SET purpose = 'sale', sale_price = '₹12,50,000' WHERE id IN (1, 3, 5);
UPDATE vehicles SET purpose = 'both', sale_price = '₹9,75,000' WHERE id IN (2, 4, 6);

-- The rest remain as 'rent' with their original rent prices


-- Log data insertion
SELECT '✅ Database initialized successfully!' as status;