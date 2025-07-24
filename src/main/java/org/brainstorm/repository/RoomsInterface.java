package org.brainstorm.repository;

import org.brainstorm.model.Rooms;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomsInterface extends JpaRepository<Rooms, Long> {

   Optional<Rooms> findByCode(String code);


}
