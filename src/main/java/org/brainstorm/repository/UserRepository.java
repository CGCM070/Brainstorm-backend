package org.brainstorm.repository;

import org.brainstorm.model.Rooms;
import org.brainstorm.model.Users;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository  extends JpaRepository<Users,Long> {

    Optional<Users> findByUsernameAndRoom(String username, Rooms room);
}
