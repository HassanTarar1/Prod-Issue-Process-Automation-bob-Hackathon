package com.hackathon.mapper;

import com.hackathon.dto.FlightDTO;
import com.hackathon.entity.Flight;

public class FlightMapper {
    
    private FlightMapper() {
        // Private constructor to prevent instantiation
    }
    
    /**
     * Converts a Flight entity to a FlightDTO
     * @param flight the Flight entity to convert
     * @return FlightDTO or null if input is null
     */
    public static FlightDTO toDTO(Flight flight) {
        if (flight == null) {
            return null;
        }
        
        FlightDTO dto = new FlightDTO();
        dto.setId(flight.getId());
        dto.setFlightNumber(flight.getFlightNumber());
        dto.setAirline(flight.getAirline());
        dto.setDestination(flight.getDestination());
        dto.setScheduledTime(flight.getScheduledTime());
        dto.setDelayMinutes(flight.getDelayMinutes());
        dto.setCancelled(flight.getCancelled());
        
        return dto;
    }
    
    /**
     * Converts a FlightDTO to a Flight entity
     * @param dto the FlightDTO to convert
     * @return Flight entity or null if input is null
     */
    public static Flight toEntity(FlightDTO dto) {
        if (dto == null) {
            return null;
        }
        
        Flight flight = new Flight();
        flight.setId(dto.getId());
        flight.setFlightNumber(dto.getFlightNumber());
        flight.setAirline(dto.getAirline());
        flight.setDestination(dto.getDestination());
        flight.setScheduledTime(dto.getScheduledTime());
        flight.setDelayMinutes(dto.getDelayMinutes());
        flight.setCancelled(dto.getCancelled());
        
        return flight;
    }
}

// Made with Bob
