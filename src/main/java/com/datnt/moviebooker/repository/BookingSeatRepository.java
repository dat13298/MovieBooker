package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.BookingSeat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingSeatRepository extends JpaRepository<BookingSeat, Long> {
    boolean existsBySeat_IdAndBooking_ShowTime_Id(Long seatId, Long showTimeId);
    void deleteByBooking_Id(Long bookingId);
    List<BookingSeat> findByBooking_Id(Long bookingId);
}
