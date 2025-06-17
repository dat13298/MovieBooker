package com.datnt.moviebooker.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.*;
import com.datnt.moviebooker.entity.Region;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
@ToString
@Table(name = "theaters")
@EntityListeners(AuditingEntityListener.class)
public class Theater extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Name can not null")
    @Size(max = 50, min = 5, message = "Name must be between 5 and 50 characters")
    private String name;

    @Column(nullable = false, unique = true, length = 50)
    @NotNull(message = "Address can not null")
    @Size(max = 50, min = 5, message = "Address must be between 5 and 50 characters")
    private String address;

    @ManyToOne
    @JoinColumn(name = "region_id", nullable = false)
    @NotNull(message = "Region can not null")
    private Region region;
}
