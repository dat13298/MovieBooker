package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "evoucher_transaction")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class EvoucherTransaction {
    @Id
    @Column(length = 36)
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(columnDefinition = "text")
    private String description;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", referencedColumnName = "id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "evoucher_id", referencedColumnName = "id")
    private Evoucher evoucher;

    @Column(name = "points", nullable = false)
    private int points;

    @Column(name = "points_after", nullable = false)
    private int pointsAfter;

    @Column(name = "points_before", nullable = false)
    private int pointsBefore;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false, length = 20)
    private TransactionType transactionType;

    public enum TransactionType {
        REDEEM, // Redeem evoucher
        EARN // Refund evoucher
    }
}
