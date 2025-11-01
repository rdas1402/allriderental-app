package com.ar.allRideRental.service;

import com.ar.allRideRental.model.User;
import com.ar.allRideRental.model.Booking;
import com.ar.allRideRental.repository.UserRepository;
import com.ar.allRideRental.repository.BookingRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class UserService {
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private BookingRepository bookingRepository;
    
    public boolean checkUserExists(String phoneNumber) {
        return userRepository.existsByPhone(phoneNumber);
    }
    
    public Optional<User> getUserByPhone(String phoneNumber) {
        return userRepository.findByPhone(phoneNumber);
    }
    
    public User createUser(String phone, String name, String email, LocalDate dob) {
        User user = new User(phone, name, email, dob);
        return userRepository.save(user);
    }
    
//    public List<Booking> getUserBookings(String phoneNumber) {
//        Optional<User> user = userRepository.findByPhone(phoneNumber);
//        return user.map(u -> bookingRepository.findByUser(u))
//                  .orElse(List.of());
//    }

    public User updateUserProfile(String phone, String name, String email, LocalDate dob) {
        Optional<User> existingUser = userRepository.findByPhone(phone);
        if (existingUser.isPresent()) {
            User user = existingUser.get();
            user.setName(name);
            user.setEmail(email);
            user.setDob(dob);
            return userRepository.save(user);
        }
        throw new RuntimeException("User not found with phone: " + phone);
    }
}