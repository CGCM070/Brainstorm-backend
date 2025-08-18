package org.brainstorm.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @NotBlank(message = "El nombre no puede estar vacío")
    @Size(min = 3, max = 25, message = "El nombre debe tener entre 3 y 10 caracteres")
    private String username;

    private boolean isOnline;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    @ToString.Exclude
    @JsonBackReference
    private Rooms room;
}
