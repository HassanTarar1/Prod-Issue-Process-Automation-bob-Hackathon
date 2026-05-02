package com.hackathon.service;

import com.hackathon.entity.Flight;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class FlightService {
    
    private List<Flight> flights = new ArrayList<>();
    
    @PostConstruct
    public void init() {
        log.info("Initializing FlightService with dummy data");
        
        // Create 50 dummy flights with intentional bugs
        String[] destinations = {"New York", "London", "Tokyo", "Paris", "Dubai", "Singapore", "Sydney", "Toronto", "Mumbai", "Berlin"};
        String[] airlines = {"Delta", "United", "British Airways", null, "Emirates", "Singapore Airlines", null, "Air Canada", "Air India", "Lufthansa"};
        
        for (int i = 1; i <= 50; i++) {
            Flight flight = new Flight();
            flight.setId((long) i);
            flight.setFlightNumber("FL" + String.format("%04d", i));
            
            // Intentional bug: Some flights have null airline
            flight.setAirline(airlines[i % airlines.length]);
            
            flight.setDestination(destinations[i % destinations.length]);
            flight.setScheduledTime(LocalDateTime.now().plusHours(i));
            
            // Intentional bug: Some flights have negative delay minutes
            if (i % 7 == 0) {
                flight.setDelayMinutes(-3);
            } else if (i % 3 == 0) {
                flight.setDelayMinutes(45);
            } else if (i % 5 == 0) {
                flight.setDelayMinutes(120);
            } else {
                flight.setDelayMinutes(0);
            }
            
            // Mix of cancelled and non-cancelled flights
            flight.setCancelled(i % 11 == 0);
            
            flights.add(flight);
        }
        
        log.info("Initialized {} flights", flights.size());
    }
    
    /**
     * Get paginated flights
     * INTENTIONAL BUG: Throws IndexOutOfBoundsException if page is out of range
     */
    public List<Flight> getFlights(int page, int size) {
        int start = page * size;
        int end = start + size;
        
        // Intentional bug: No bounds checking - will throw IndexOutOfBoundsException
        return flights.subList(start, end);
    }
    
    /**
     * Get airline name in uppercase
     * INTENTIONAL BUG: Throws NullPointerException when airline is null
     */
    public String getAirlineUpperCase(Flight flight) {
        // Intentional bug: No null check before calling toUpperCase()
        return flight.getAirline().toUpperCase();
    }
    
    /**
     * Calculate percentage of delayed flights
     * INTENTIONAL BUG: Throws ArithmeticException if total flights == 0
     */
    public double getDelayedPercentage() {
        int totalFlights = flights.size();
        long delayedCount = flights.stream()
                .filter(f -> f.getDelayMinutes() != null && f.getDelayMinutes() > 0)
                .count();
        
        // Intentional bug: No division by zero check
        return (delayedCount / totalFlights) * 100;
    }
    
    /**
     * Parse flight date string
     * INTENTIONAL BUG: Throws ParseException for bad timezone format
     */
    public LocalDateTime parseFlightDate(String dateStr) {
        // Intentional bug: Strict pattern that will fail on certain inputs
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        
        // No try-catch or validation - will throw DateTimeParseException
        return LocalDateTime.parse(dateStr, formatter);
    }
    
    /**
     * Get all flights
     * No bugs in this method
     */
    public List<Flight> getAllFlights() {
        return new ArrayList<>(flights);
    }
}

// Made with Bob