//package com.datnt.moviebooker.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//@Entity
//@Table(name = "banner_images")
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//@Setter
//@ToString
//@EntityListeners(AuditingEntityListener.class)
//public class BannerImage extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, name = "image_url")
//    @NotNull(message = "Image URL cannot be null")
//    private String imageUrl;
//
//    @Column(nullable = false, length = 100)
//    @NotNull(message = "Title cannot be null")
//    private String title;
//
//    @Column(nullable = false, length = 255)
//    @NotNull(message = "Description cannot be null")
//    private String description;
//}
//package com.datnt.moviebooker.entity;
//
//import jakarta.persistence.*;
//import jakarta.validation.constraints.NotNull;
//import lombok.*;
//import org.springframework.data.jpa.domain.support.AuditingEntityListener;
//
//@Entity
//@Table(name = "banner_images")
//@AllArgsConstructor
//@NoArgsConstructor
//@Builder
//@Getter
//@Setter
//@ToString
//@EntityListeners(AuditingEntityListener.class)
//public class BannerImage extends BaseEntity {
//    @Id
//    @GeneratedValue(strategy = GenerationType.IDENTITY)
//    private Long id;
//
//    @Column(nullable = false, name = "image_url")
//    @NotNull(message = "Image URL cannot be null")
//    private String imageUrl;
//
//    @Column(nullable = false, length = 100)
//    @NotNull(message = "Title cannot be null")
//    private String title;
//
//    @Column(nullable = false, length = 255)
//    @NotNull(message = "Description cannot be null")
//    private String description;
//}
