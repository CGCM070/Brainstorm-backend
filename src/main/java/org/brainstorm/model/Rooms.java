package org.brainstorm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
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

    @NotBlank(message = "El título no puede estar vacío")
    @Size(min = 3, max = 25, message = "El título debe tener entre 3 y 20 caracteres")
    private String title ;

    @Column(length = 6)
    private String code ;

    @Column(length = 45)
    private String createdBy;

    @Column(length = 45)
    private LocalDateTime createdAt = LocalDateTime.now();

    private Integer maxUsers =50;

    @OneToMany (mappedBy = "room", cascade = CascadeType.ALL)
    @Builder.Default
    @JsonManagedReference
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
