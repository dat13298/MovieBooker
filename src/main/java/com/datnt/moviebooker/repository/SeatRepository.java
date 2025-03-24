package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Seat;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    List<Seat> findSeatsByScreen_Id(Long screenId);
}
