package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RegionRepository extends JpaRepository<Region, Long> {

    // No additional methods needed for now, as BaseRepository provides basic CRUD operations.
    // You can add custom query methods here if needed in the future.
}
