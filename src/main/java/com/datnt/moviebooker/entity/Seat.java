package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;

@Entity
@Table(name = "seats")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class Seat {

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
}
