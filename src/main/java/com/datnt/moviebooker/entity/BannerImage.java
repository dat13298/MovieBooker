package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.*;

@Entity
@Table(name = "banner_images")
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
public class BannerImage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, name = "image_url")
    @NotNull(message = "Image URL cannot be null")
    private String imageUrl;

    @Column(nullable = false, length = 100)
    @NotNull(message = "Title cannot be null")
    private String title;

    @Column(nullable = false, length = 255)
    @NotNull(message = "Description cannot be null")
    private String description;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @NotNull(message = "User cannot be null")
    private User user;
}
