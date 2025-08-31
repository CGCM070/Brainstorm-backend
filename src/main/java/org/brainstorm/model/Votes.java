package org.brainstorm.model;


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

    @Builder.Default
    private Integer totalVotes = 0;

    private Instant createdAt = Instant.now();
    private Instant  updatedAt ;

    private Integer voteValue;

    @ManyToOne
    @JoinColumn(name = "idea_id")
    @ToString.Exclude
    private Ideas idea;

    @ManyToOne
    @JoinColumn(name = "user_id")
    @ToString.Exclude
    private Users user;
}
