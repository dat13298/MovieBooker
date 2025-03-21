package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.ShowTime;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ShowTimeRepository extends JpaRepository<ShowTime, Long> {
}
