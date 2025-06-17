package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.constant.SeatType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@EntityListeners(AuditingEntityListener.class)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", nullable = false, unique = true, length = 5)
    @NotNull(message = "Seat Number can not null")
    @Size(max = 5, min = 2, message = "Seat Number must be between 5 and 50 characters")
    private String seatNumber;

    @ManyToOne
    @JoinColumn(name = "screen_id")
    @NotNull(message = "Screen can not null")
    private Screen screen;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @NotNull(message = "Seat Status can not null")
    private SeatStatus status;

    @ManyToOne
    @JoinColumn(name = "show_time_id")
    @NotNull(message = "Show Time can not null")
    private ShowTime showTime;

    @Column(name = "price", nullable = false)
    @NotNull(message = "Price can not null")
    private Long price;

    @Column(name = "seat_type", nullable = false)
    @Enumerated(EnumType.STRING)
    @NotNull(message = "Seat Type can not null")
    private SeatType seatType;
}
