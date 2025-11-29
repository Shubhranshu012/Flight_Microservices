package com.flightservice.controller;

import java.util.HashMap;
import java.util.Map;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import com.flightservice.dto.InventoryRequestDto;
import jakarta.validation.Valid;

@Controller
public class AirlineInventoryController {
	
	@PostMapping("/api/flight/airline/inventory")
	public ResponseEntity<Map<String, String>> inventory(@RequestBody @Valid InventoryRequestDto inventoryDto) {
		Map<String,String> responce= new HashMap<>();
		responce.put(null, null);
		return ResponseEntity.status(HttpStatus.CREATED).body(responce);
	}

}
