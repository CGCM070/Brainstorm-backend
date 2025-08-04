package org.brainstorm.repository;

import org.brainstorm.model.Ideas;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface IdeaRepository extends JpaRepository<Ideas, Long> {


}
