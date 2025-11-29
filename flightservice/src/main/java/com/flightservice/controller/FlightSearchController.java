package com.flightservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.flightservice.dto.SearchRequestDto;
import com.flightservice.model.FlightInventory;
import com.flightservice.service.implementation.FlightServiceImplment;

import jakarta.validation.Valid;

import java.util.List;
import java.util.Map;

@RestController
public class FlightSearchController {
	
	
	@Autowired
	FlightServiceImplment inventoryService;
	
    @PostMapping("/api/flight/search")
    public ResponseEntity<Map<String, List<FlightInventory>>> search(@RequestBody @Valid SearchRequestDto dto) {
        return ResponseEntity.ok(inventoryService.searchFlights(dto));
    }
    
    @GetMapping("/api/flight/search/{flightNumber}")
    public FlightInventory searchFlight(@PathVariable String flightNumber) {
    	return inventoryService.searchFlightBasedOnFlightNumber(flightNumber);
    }
}