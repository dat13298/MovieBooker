package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.Status;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table(name = "bookings")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Booking extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @NotNull(message = "User can not null")
    private User user;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    @NotNull(message = "Show Time can not null")
    private ShowTime showTime;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private Status status;

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<BookingSeat> bookingSeats = new ArrayList<>();

    @OneToMany(mappedBy = "booking", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private Set<FoodBooking> foodBookings = new HashSet<>();

    @Column(name = "txn_number")
    private String txnNumber;

    @Column(name = "total_amount", nullable = false, updatable = false)
    private Long totalAmount;

    @Column(name = "booking_code", unique = true, nullable = false, updatable = false, length = 20)
    private String bookingCode;
}
