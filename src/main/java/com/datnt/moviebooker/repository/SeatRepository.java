package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Seat;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SeatRepository extends JpaRepository<Seat, Long> {
    Page<Seat> findSeatsByScreen_Id(Long screenId, Pageable pageable);
}
