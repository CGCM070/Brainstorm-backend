package org.brainstorm.model;


import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;

@Builder
@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(onlyExplicitlyIncluded = true)

public class Votes {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;
    
    private Instant createdAt = Instant.now();
    private Instant  updatedAt ;

    private Integer voteValue;

    @ManyToOne
    @JoinColumn(name = "idea_id")
    @ToString.Exclude
    @JsonIgnore
    private Ideas idea;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Users user;
}
