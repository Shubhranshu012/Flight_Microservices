package com.flightservice.service.implementation;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.model.AIRPORT_NAME;
import com.flightservice.model.Flight;
import com.flightservice.model.FlightInventory;
import com.flightservice.repository.FlightInventoryRepository;
import com.flightservice.repository.FlightRepository;
import com.flightservice.service.FlightService;

@Service
public class FlightServiceImplment implements FlightService {
	
	
	@Autowired
	FlightInventoryRepository inventoryRepo;
	
	@Autowired 
	FlightRepository flightRepo;
	
	public FlightInventory addInventory(InventoryRequestDto inventoryDto) {
    	
    	if (inventoryDto.getAvailableSeats() > inventoryDto.getTotalSeats()) {
            throw new RuntimeException("Available seats cannot be greater than total seats");
        }
    	if (inventoryDto.getArrivalTime().isBefore(inventoryDto.getDepartureTime())) {
    	    throw new RuntimeException("Arrival time cannot be before departure time");
    	}
    	if(inventoryDto.getFromPlace().equals(inventoryDto.getToPlace())) {
    		throw new RuntimeException("From To Cant be Same");
    	}
    	
    	Optional<FlightInventory> duplicate = inventoryRepo.findByAirlineAndFlightIdAndSourceAndDestinationAndDepartureTime(inventoryDto.getAirlineName(),inventoryDto.getFlightNumber(),AIRPORT_NAME.valueOf(inventoryDto.getFromPlace()),AIRPORT_NAME.valueOf(inventoryDto.getToPlace()),inventoryDto.getDepartureTime());

    	if (duplicate.isPresent()) {
    	        throw new RuntimeException("Flight already exists with same details (airline, flightNumber, route, departureTime)");
    	}
    	
    	Flight flight = flightRepo.findById(inventoryDto.getFlightNumber()).orElseGet(() -> {
    			Flight f = Flight.builder().flightNumber(inventoryDto.getFlightNumber()).airlineName(inventoryDto.getAirlineName()).fromPlace(AIRPORT_NAME.valueOf(inventoryDto.getFromPlace())).toPlace(AIRPORT_NAME.valueOf(inventoryDto.getToPlace())).build();
    			return flightRepo.save(f);
    	});


    	FlightInventory fi = FlightInventory.builder().flightId(inventoryDto.getFlightNumber()).departureTime(inventoryDto.getDepartureTime()).arrivalTime(inventoryDto.getArrivalTime()).price(inventoryDto.getPrice())
    			.totalSeats(inventoryDto.getTotalSeats()).availableSeats(inventoryDto.getAvailableSeats()).build();

    	return inventoryRepo.save(fi);
    }

}
