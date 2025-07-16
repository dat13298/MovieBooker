package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Booking;
import com.datnt.moviebooker.entity.User;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<Booking, Long> {
    Page<Booking> findByUser_Id(Long userId, Pageable pageable);

    @Query("""
    SELECT DISTINCT b FROM Booking b
    LEFT JOIN FETCH b.bookingSeats bs
    LEFT JOIN FETCH bs.seat
    LEFT JOIN FETCH b.foodBookings fb
    WHERE b.user.id = :userId
    ORDER BY b.createdAt DESC
""")
    List<Booking> findByUserIdWithDetails(@Param("userId") Long userId);
}
