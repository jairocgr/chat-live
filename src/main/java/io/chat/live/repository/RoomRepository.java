package io.chat.live.repository;

import io.chat.live.domain.Room;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoomRepository extends JpaRepository<Room, Integer> {

    Optional<Room> findByHandle(String handle);

    boolean existsByHandle(String handle);

    @Query("SELECT r FROM Room r WHERE r.general is TRUE")
    Room getGeneralRoom();
}
