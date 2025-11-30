package com.bookingservive.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.bookingservive.dto.BookingRequestDto;
import com.bookingservive.service.implementation.BookingServiceImplementation;

import jakarta.validation.Valid;

@Controller
public class BookingController {
	@Autowired
	BookingServiceImplementation bookingService;
	
	@PostMapping("/api/flight/booking/{flightId}")
	public ResponseEntity<Object> book(@PathVariable String flightId,@RequestBody @Valid BookingRequestDto bookingDto) {
		return null;
	    //return bookingService.bookTicket(flightId, bookingDto).map(response -> ResponseEntity.status(HttpStatus.CREATED).body(response));
	}
}
