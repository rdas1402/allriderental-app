package com.ar.allRideRental.repository;

import com.ar.allRideRental.model.City;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface CityRepository extends JpaRepository<City, Long> {
    List<City> findByIsActiveTrue();
    Optional<City> findByName(String name);
    boolean existsByName(String name);
}