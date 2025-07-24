package org.brainstorm.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;


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
    private String code;

    @Column(length = 45)
    private String createdBy;

    @Column(length = 45)
    private LocalDateTime createdAt;

    private boolean isActive;
    private Integer maxUsers;

    @OneToMany (mappedBy = "room", cascade = CascadeType.ALL)
    Set<Users> users=  new HashSet<>();

    @OneToMany (mappedBy = "room", cascade = CascadeType.ALL)
    Set<Ideas> ideas = new HashSet<>();

}
