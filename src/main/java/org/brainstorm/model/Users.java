package org.brainstorm.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
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

    @Column(length = 45, unique = true)
    private String username;

    private boolean isOnline;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = true)
    @ToString.Exclude
    @JsonBackReference
    private Rooms room;
}
