package com.datnt.moviebooker.entity;


import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@Table(name = "food_services")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@EntityListeners(AuditingEntityListener.class)
public class ComboFood extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    @NotNull(message = "Name can not be null")
    private String name;

    @Column(length = 500)
    @NotNull(message = "Description can not be null")
    private String description;

    @Column(nullable = false)
    @NotNull(message = "Price can not be null")
    private Long price;

    private String imageUrl;

    private Boolean isActive;

    private String foodType; // e.g., "snack", "drink", etc.
}
