package com.hackathon.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDateTime;

@Data
public class FlightDTO {
    
    private Long id;
    
    @NotBlank(message = "Flight number is required")
    private String flightNumber;
    
    private String airline;
    
    @NotBlank(message = "Destination is required")
    private String destination;
    
    @NotNull(message = "Scheduled time is required")
    private LocalDateTime scheduledTime;
    
    @NotNull(message = "Delay minutes is required")
    private Integer delayMinutes;
    
    @NotNull(message = "Cancelled status is required")
    private Boolean cancelled;
}

// Made with Bob
