package com.datnt.moviebooker.dto;

import java.util.List;

public record BookingRequest(Long showTimeId, List<Long> seatIds) {}
