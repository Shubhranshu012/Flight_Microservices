package com.bookingservive.service.implementation;

import org.springframework.stereotype.Service;

import com.bookingservive.dto.BookingRequestDto;
import com.bookingservive.service.BookingService;

@Service
public class BookingServiceImplementation implements BookingService {
	public Object bookTicket(String flightId, BookingRequestDto bookingDto) {
		return bookingDto;
	}
}	
