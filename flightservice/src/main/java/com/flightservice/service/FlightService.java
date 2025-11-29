package com.flightservice.service;

import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.model.FlightInventory;

public interface FlightService {
	public FlightInventory addInventory(InventoryRequestDto dto);
}
