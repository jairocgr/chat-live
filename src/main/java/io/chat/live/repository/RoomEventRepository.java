package io.chat.live.repository;

import io.chat.live.domain.RoomEvent;
import jakarta.persistence.QueryHint;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.jpa.repository.QueryHints;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Stream;

import static org.hibernate.jpa.HibernateHints.HINT_FETCH_SIZE;


@Repository
public interface RoomEventRepository extends JpaRepository<RoomEvent, Integer> {

    @Query("SELECT e FROM RoomEvent e WHERE e.room.handle = ?1 and e.id < ?2 ORDER BY e.id DESC LIMIT ?3")
    List<RoomEvent> before(String room, int oldestId, int limit);

    @QueryHints(value = { @QueryHint(name = HINT_FETCH_SIZE, value = "64") })
    @Query("SELECT e FROM RoomEvent e WHERE e.room.handle = ?1 and e.id > ?2 ORDER BY e.id")
    Stream<RoomEvent> after(String room, int lastKnownId);

    @Query("SELECT e FROM RoomEvent e WHERE e.room.handle = ?1 ORDER BY e.id DESC LIMIT 1")
    RoomEvent lastEventFrom(String room);
}




