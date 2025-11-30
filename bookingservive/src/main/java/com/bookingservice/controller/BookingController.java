package com.bookingservice.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.bookingservice.dto.BookingRequestDto;
import com.bookingservice.model.Booking;
import com.bookingservice.service.implementation.BookingServiceImplementation;

import jakarta.validation.Valid;

@RestController
public class BookingController {
	@Autowired
	BookingServiceImplementation bookingService;

	@PostMapping("/api/flight/booking/{flightId}")
	public ResponseEntity<Booking> book(@PathVariable String flightId, @RequestBody @Valid BookingRequestDto bookingDto) {
		Booking booking = bookingService.bookTicket(flightId, bookingDto);
		return ResponseEntity.status(HttpStatus.CREATED).body(booking);
	}
}
