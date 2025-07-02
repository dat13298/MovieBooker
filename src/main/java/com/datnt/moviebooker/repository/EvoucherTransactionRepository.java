package com.datnt.moviebooker.repository;

import com.datnt.moviebooker.entity.EvoucherTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface EvoucherTransactionRepository extends JpaRepository<EvoucherTransaction, Long> {
    // Define custom query methods if needed
    @Query("SELECT et FROM EvoucherTransaction et WHERE et.user.id = :userId AND " +
            "(:startDate IS NULL AND :endDate IS NULL AND et.transactionDate >= :defaultStartDate) OR " +
            "(:startDate IS NOT NULL AND :endDate IS NOT NULL AND et.transactionDate BETWEEN :startDate AND :endDate) " +
            "ORDER BY et.transactionDate DESC")
    List<EvoucherTransaction> findTransactionHistory(
            @Param("userId") Long userId,
            @Param("startDate") LocalDateTime startDate,
            @Param("endDate") LocalDateTime endDate,
            @Param("defaultStartDate") LocalDateTime defaultStartDate
    );

    @Query(value = "SELECT * FROM evoucher_transaction et WHERE et.user_id = :userId", nativeQuery = true)
    EvoucherTransaction findByUserId(@Param("userId") Long userId);
}
