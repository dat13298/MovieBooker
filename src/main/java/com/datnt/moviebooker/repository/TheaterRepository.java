package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Movie;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TheaterRepository extends JpaRepository<Movie, Long> {
}
