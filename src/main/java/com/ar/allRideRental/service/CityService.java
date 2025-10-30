package com.ar.allRideRental.service;

import com.ar.allRideRental.model.City;
import com.ar.allRideRental.repository.CityRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class CityService {
    
    @Autowired
    private CityRepository cityRepository;
    
    public List<City> getAllCities() {
        return cityRepository.findByIsActiveTrue();
    }
    
    public Optional<City> getCityById(Long id) {
        return cityRepository.findById(id);
    }
    
    public Optional<City> getCityByName(String name) {
        return cityRepository.findByName(name);
    }
    
    public City createCity(City city) {
        if (cityRepository.existsByName(city.getName())) {
            throw new RuntimeException("City already exists: " + city.getName());
        }
        return cityRepository.save(city);
    }
    
    public City updateCity(Long id, City cityDetails) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));
        
        city.setName(cityDetails.getName());
        city.setState(cityDetails.getState());
        city.setActive(cityDetails.isActive());
        
        return cityRepository.save(city);
    }
    
    public void deleteCity(Long id) {
        City city = cityRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("City not found with id: " + id));
        city.setActive(false);
        cityRepository.save(city);
    }
}