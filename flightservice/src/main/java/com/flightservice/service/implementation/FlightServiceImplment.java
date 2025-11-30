package com.flightservice.service.implementation;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.flightservice.dto.InventoryRequestDto;
import com.flightservice.dto.SearchRequestDto;
import com.flightservice.exception.BadRequestException;
import com.flightservice.exception.NotFoundException;
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
            throw new BadRequestException("Available seats cannot be greater than total seats");
        }
    	if (inventoryDto.getArrivalTime().isBefore(inventoryDto.getDepartureTime())) {
    	    throw new BadRequestException("Arrival time cannot be before departure time");
    	}
    	if(inventoryDto.getFromPlace().equals(inventoryDto.getToPlace())) {
    		throw new BadRequestException("From To Cant be Same");
    	}
    	
    	Optional<FlightInventory> duplicate = inventoryRepo.findByAirlineAndFlightIdAndSourceAndDestinationAndDepartureTime(inventoryDto.getAirlineName(),inventoryDto.getFlightNumber(),AIRPORT_NAME.valueOf(inventoryDto.getFromPlace()),AIRPORT_NAME.valueOf(inventoryDto.getToPlace()),inventoryDto.getDepartureTime());

    	if (duplicate.isPresent()) {
    	        throw new BadRequestException("Flight already exists with same details (airline, flightNumber, route, departureTime)");
    	}
    	
    	flightRepo.findById(inventoryDto.getFlightNumber()).orElseGet(() -> {
    			Flight newFlight = Flight.builder().flightNumber(inventoryDto.getFlightNumber()).airlineName(inventoryDto.getAirlineName()).fromPlace(AIRPORT_NAME.valueOf(inventoryDto.getFromPlace())).toPlace(AIRPORT_NAME.valueOf(inventoryDto.getToPlace())).build();
    			return flightRepo.save(newFlight);
    	});

    	FlightInventory fi = FlightInventory.builder().flightId(inventoryDto.getFlightNumber()).departureTime(inventoryDto.getDepartureTime()).arrivalTime(inventoryDto.getArrivalTime()).price(inventoryDto.getPrice())
    			.totalSeats(inventoryDto.getTotalSeats()).availableSeats(inventoryDto.getAvailableSeats()).source(AIRPORT_NAME.valueOf(inventoryDto.getFromPlace())).destination(AIRPORT_NAME.valueOf(inventoryDto.getToPlace())).airline(inventoryDto.getAirlineName()).build();

    	return inventoryRepo.save(fi);
    }
	
	public Map<String, List<FlightInventory>> searchFlights(SearchRequestDto dto) {

        Map<String, List<FlightInventory>> response = new HashMap<>();

        LocalDateTime onwardStart = dto.getJourneyDate().atStartOfDay();
        LocalDateTime onwardEnd = dto.getJourneyDate().atTime(23, 59, 59);

        List<FlightInventory> onwardFlights =inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(AIRPORT_NAME.valueOf(dto.getFromPlace()),AIRPORT_NAME.valueOf(dto.getToPlace()),onwardStart,onwardEnd );

        if (onwardFlights.isEmpty()) {
            throw new NotFoundException();
        }

        response.put("onwardFlights", onwardFlights);

        if (dto.getTripType().equalsIgnoreCase("ROUND_TRIP")) {

            if (dto.getReturnDate() == null) {
                throw new BadRequestException("Return date is required for ROUND_TRIP");
            }

            LocalDateTime returnStart = dto.getReturnDate().atStartOfDay();
            LocalDateTime returnEnd = dto.getReturnDate().atTime(23, 59, 59);

            List<FlightInventory> returnFlights =inventoryRepo.findBySourceAndDestinationAndDepartureTimeBetween(AIRPORT_NAME.valueOf(dto.getToPlace()),AIRPORT_NAME.valueOf(dto.getFromPlace()),returnStart,returnEnd);

            if (returnFlights.isEmpty()) {
                throw new NotFoundException();
            }

            response.put("returnFlights", returnFlights);
        }

        return response;
    }
	
	public FlightInventory searchFlightBasedOnFlightNumber(String flightNumber){
		Optional<FlightInventory> inventory=inventoryRepo.findById(flightNumber);
		if(inventory.isEmpty()) {
			throw new NotFoundException();
		}
		return inventory.get();
	}
	public String changeAvaliableSeat(String flightNumber, Integer seat) {

	    Optional<FlightInventory> currentInventory = inventoryRepo.findById(flightNumber);

	    if (currentInventory.isEmpty()) {
	        throw new NotFoundException();
	    }
	    currentInventory.get().setAvailableSeats(seat);
	    inventoryRepo.save(currentInventory.get());
	    return "Available seats updated successfully for flight " + flightNumber;
	}
}
