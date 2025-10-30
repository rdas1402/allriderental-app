package com.ar.allRideRental.model;

import jakarta.persistence.*;

@Entity
@Table(name = "cities")
public class City {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Column(nullable = false)
    private String state;
    
    private boolean isActive = true;
    
    // Constructors
    public City() {}
    
    public City(String name, String state) {
        this.name = name;
        this.state = state;
    }
    
    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    
    public String getState() { return state; }
    public void setState(String state) { this.state = state; }
    
    public boolean isActive() { return isActive; }
    public void setActive(boolean active) { isActive = active; }
}