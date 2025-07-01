package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PointRepository extends JpaRepository<Point, Long> {
    Optional<Point> findByUserId(Long id);

    @Query(value = """
            SELECT 
                *
            FROM points p
            WHERE p.user_id = :userId
            """, nativeQuery = true)
    Optional<Point> getPointByUserId(@Param("userId") Long userId);
}
