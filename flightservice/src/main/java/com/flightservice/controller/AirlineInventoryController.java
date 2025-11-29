package com.flightservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.service.implementation.FlightServiceImplment;

import jakarta.validation.Valid;

@Controller
public class AirlineInventoryController {
	
	@Autowired 
	FlightServiceImplment flightInplement;
	
	@PostMapping("/api/flight/airline/inventory")
	public ResponseEntity<String> inventory(@RequestBody @Valid InventoryRequestDto inventoryDto) {
		flightInplement.addInventory(inventoryDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(null);
	}

}
