package com.hackathon.controller;

import com.hackathon.dto.FlightDTO;
import com.hackathon.entity.Flight;
import com.hackathon.mapper.FlightMapper;
import com.hackathon.service.FlightService;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RestController
@RequestMapping("/api")
@CrossOrigin
@Validated
public class FlightController {
    
    private final FlightService flightService;
    
    public FlightController(FlightService flightService) {
        this.flightService = flightService;
    }
    
    /**
     * Get paginated flights
     * @param page Page number (default 0, min 0)
     * @param size Page size (default 5, min 1, max 100)
     * @return List of FlightDTOs
     */
    @GetMapping("/flights")
    public ResponseEntity<List<FlightDTO>> getFlights(
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "5") @Min(1) @Max(100) int size) {
        
        log.info("Getting flights - page: {}, size: {}", page, size);
        
        List<Flight> flights = flightService.getFlights(page, size);
        List<FlightDTO> flightDTOs = flights.stream()
                .map(FlightMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(flightDTOs);
    }
    
    /**
     * Get delayed flights percentage
     * @return Map containing the percentage of delayed flights
     */
    @GetMapping("/stats/delayed-percentage")
    public ResponseEntity<Map<String, Double>> getDelayedPercentage() {
        log.info("Getting delayed percentage");
        
        double percentage = flightService.getDelayedPercentage();
        
        return ResponseEntity.ok(Map.of("percentage", percentage));
    }
    
    /**
     * Parse flight date string
     * @param request Map containing "dateStr" key
     * @return Map containing the parsed date
     */
    @PostMapping("/flights/parse-date")
    public ResponseEntity<Map<String, String>> parseFlightDate(@RequestBody Map<String, String> request) {
        String dateStr = request.get("dateStr");
        log.info("Parsing flight date: {}", dateStr);
        
        LocalDateTime parsedDate = flightService.parseFlightDate(dateStr);
        String formattedDate = parsedDate.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        
        return ResponseEntity.ok(Map.of("parsedDate", formattedDate));
    }
    
    /**
     * Get all flights
     * @return List of all FlightDTOs
     */
    @GetMapping("/flights/all")
    public ResponseEntity<List<FlightDTO>> getAllFlights() {
        log.info("Getting all flights");
        
        List<Flight> flights = flightService.getAllFlights();
        List<FlightDTO> flightDTOs = flights.stream()
                .map(FlightMapper::toDTO)
                .collect(Collectors.toList());
        
        return ResponseEntity.ok(flightDTOs);
    }
}

// Made with Bob