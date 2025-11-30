package com.bookingservice.service.implementation;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.dto.FlightInventoryDto;
import com.bookingservice.dto.PassengerDto;
import com.bookingservice.feign.FlightInterface;
import com.bookingservice.model.BOOKING_STATUS;
import com.bookingservice.model.Booking;
import com.bookingservice.model.GENDER;
import com.bookingservice.model.Passenger;
import com.bookingservice.repository.BookingRepository;
import com.bookingservice.repository.PassengerRepository;
import com.bookingservice.service.BookingService;

@Service
public class BookingServiceImplementation implements BookingService {
	
	@Autowired
	FlightInterface flightClient;
	
	@Autowired
	BookingRepository bookingRepo;

	@Autowired
	PassengerRepository passengerRepo;

	public Booking bookTicket(String inventoryId, BookingRequestDto bookingDto) {

		String pnr = "PNR"+UUID.randomUUID().toString().substring(0, 8).toUpperCase();

		ResponseEntity<FlightInventoryDto> flightResponse = flightClient.searchFlight(inventoryId);
		if (!flightResponse.getStatusCode().is2xxSuccessful() || flightResponse.getBody() == null) {
			throw new RuntimeException("Flight not found: " + inventoryId);
		}

		FlightInventoryDto flightInventory = flightResponse.getBody();
		System.out.println("Flight inventory: " + flightInventory);

		Booking booking = Booking.builder()
				.pnr(pnr)
				.email(bookingDto.getEmail())
				.bookingTime(LocalDateTime.now())
				.departureTime(flightInventory.getDepartureTime())
				.arrivalTime(flightInventory.getArrivalTime())
				.flightInventoryId(inventoryId)
				.status(BOOKING_STATUS.BOOKED)
				.build();

		Booking savedBooking = bookingRepo.save(booking);

		List<Passenger> passengers = bookingDto.getPassengers().stream()
				.map(passengerDto -> createPassenger(passengerDto, savedBooking.getId(), inventoryId))
				.collect(Collectors.toList());

		passengerRepo.saveAll(passengers);
		int seatsToBook = bookingDto.getPassengers().size();
		try {
			ResponseEntity<Map<String, String>> updateResponse = flightClient.updateAvailableSeat(inventoryId, seatsToBook);
			if (!updateResponse.getStatusCode().is2xxSuccessful()) {
				System.err.println("Failed to update seat availability for flight: " + inventoryId);
			}
		} catch (Exception e) {
			System.err.println("Error updating seat availability: " + e.getMessage());
		}

		return savedBooking;
	}

	private Passenger createPassenger(PassengerDto dto, String bookingId, String flightId) {
	    return Passenger.builder()
	            .bookingId(bookingId)
	            .flightInventoryId(flightId)
	            .name(dto.getName())
	            .gender(GENDER.valueOf(dto.getGender().toUpperCase()))
	            .age(dto.getAge())
	            .seatNumber(dto.getSeatNumber())
	            .mealOption(dto.getMealOption())
	            .build();
	}

}
