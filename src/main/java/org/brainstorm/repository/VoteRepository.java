package org.brainstorm.repository;

import org.brainstorm.model.Ideas;
import org.brainstorm.model.Users;
import org.brainstorm.model.Votes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface VoteRepository  extends JpaRepository<Votes,Long > {


    Optional<Votes> findByIdeaAndUser(Ideas idea, Users user);

    void deleteByIdeaAndUser(Ideas idea, Users user);
}
