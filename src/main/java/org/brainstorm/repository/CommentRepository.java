package org.brainstorm.repository;

import org.brainstorm.model.Comments;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentRepository  extends JpaRepository<Comments, Long> {

    List<Comments> findByIdeaId(Long ideaId);

}
