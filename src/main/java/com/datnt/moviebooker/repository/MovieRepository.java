package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.constant.MovieStatus;
import com.datnt.moviebooker.entity.Movie;
import org.springframework.data.repository.query.Param;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface MovieRepository extends JpaRepository<Movie, Long> {
    @Query("""
    SELECT m FROM Movie m
    WHERE (:kw IS NULL  OR :kw  = '' OR
           lower(m.movieName) LIKE lower(concat('%', :kw, '%')) OR
           lower(m.movieCode) LIKE lower(concat('%', :kw, '%')) OR
           lower(m.actors)    LIKE lower(concat('%', :kw, '%'))
          )
      AND (:st    IS NULL OR m.movieStatus  = :st)
      AND (:scr   IS NULL OR m.screenType   = :scr)
      AND (:adult IS NULL OR m.eighteenPlus = :adult)
""")
    Page<Movie> search(@Param("kw")   String kw,
                       @Param("st")   MovieStatus st,
                       @Param("scr")  String screenType,
                       @Param("adult")Boolean adult,
                       Pageable pageable);

}
