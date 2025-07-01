package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.context.annotation.Lazy;

@EqualsAndHashCode(callSuper = true)
@Entity
@Table(name = "points")
@Data
public class Point extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "available_points", nullable = false)
    private int availablePoints = 0;

    @Column(name = "redeemed_points", nullable = false)
    private int redeemedPoints = 0;

    @Column(name = "total_points", nullable = false)
    private int totalPoints = 0;

    @OneToOne
    @JoinColumn(name = "user_id", referencedColumnName = "id")
    private User user;
}
