package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.Evoucher;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EvoucherRepository extends JpaRepository<Evoucher, Long> {
    @Query(value = "SELECT * FROM evoucher WHERE created_by = :createdBy ORDER BY created_at DESC", nativeQuery = true)
    List<Evoucher> findByCreatedBy(@Param("createdBy") String createdBy);
}
