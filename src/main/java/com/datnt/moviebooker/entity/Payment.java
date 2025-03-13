package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.PaymentMethod;
import com.datnt.moviebooker.constant.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.sql.Timestamp;

@Entity
@Table(name = "payments")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "booking_id")
    @NotNull(message = "Booking can not null")
    private Booking booking;

    @Column(nullable = false)
    @NotNull(message = "Amount can not null")
    @Min(value = 0)
    private BigDecimal amount;

    @Column(nullable = false)
    @NotNull(message = "Payment Method can not null")
    @Enumerated(EnumType.STRING)
    private PaymentMethod paymentMethod;

    @Column(name = "transaction_id", nullable = false, unique = true)
    private String transactionId;

    @Column(nullable = false)
    private Status status;

    @Column(name = "created_at", nullable = false, updatable = false)
    @CreatedDate
    private Timestamp createdAt;
}
