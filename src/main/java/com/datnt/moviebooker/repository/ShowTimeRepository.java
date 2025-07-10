package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {
    List<ShowTime> findByMovieIdAndStartTimeAfter(Long movieId, LocalDateTime now);

}
