package com.datnt.moviebooker.entity;

import com.datnt.moviebooker.constant.SeatStatus;
import com.datnt.moviebooker.constant.SeatType;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "seats",
        uniqueConstraints = @UniqueConstraint(
                columnNames = {"screen_id","row_idx","col_idx"}))
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EntityListeners(AuditingEntityListener.class)
public class Seat extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "seat_number", length = 5, nullable = false)
    private String seatNumber;

    @Column(name = "row_idx", nullable = false)
    private Integer row;

    @Column(name = "col_idx", nullable = false)
    private Integer col;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "screen_id", nullable = false)
    private Screen screen;

    @ManyToOne
    @JoinColumn(name = "show_time_id", nullable = false)
    private ShowTime showTime;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SeatStatus status;

    @Enumerated(EnumType.STRING)
    @Column(name = "seat_type", nullable = false)
    private SeatType seatType;

    @Column(name = "price")
    private Long price;
}
