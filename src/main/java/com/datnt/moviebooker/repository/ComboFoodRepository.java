package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.ComboFood;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ComboFoodRepository extends JpaRepository<ComboFood, Long> {
    List<ComboFood> findByIsActiveTrue();
}
