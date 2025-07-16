package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.FoodBooking;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface FoodBookingRepository extends JpaRepository<FoodBooking, Long> {
    void deleteByBooking_Id(Long bookingId);
}
