package org.brainstorm.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Rooms {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(length = 6)
    private String code ;

    @Column(length = 45)
    private String createdBy;

    @Column(length = 45)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer maxUsers;

    @OneToMany (mappedBy = "room", cascade = CascadeType.ALL)
    @Builder.Default
    Set<Users> users=  new HashSet<>();

    @OneToMany (mappedBy = "room", cascade = CascadeType.ALL)
    @Builder.Default
    Set<Ideas> ideas = new HashSet<>();

    @PrePersist
    private void assignCode() {
        if (this.code == null) {
            this.code = UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        }
    }

}
