package com.bookingservive.service;

import com.bookingservive.dto.BookingRequestDto;

public interface BookingService {
	public Object bookTicket(String flightId, BookingRequestDto bookingDto);
}
