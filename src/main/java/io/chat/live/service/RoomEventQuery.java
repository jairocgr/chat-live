package io.chat.live.service;

import io.chat.live.domain.RoomEvent;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.repository.RoomEventRepository;
import jakarta.persistence.EntityManager;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Stream;

@Service
@RequiredArgsConstructor
public class RoomEventQuery {

    private static final int PAGE_SIZE = 64;

    private final RoomEventRepository events;
    private final EntityManager em;

    public List<RoomEventDTO> before(String room, int oldestId) {
        return events.before(room, oldestId, PAGE_SIZE).stream()
            .map(RoomEvent::toDTO)
            .toList();
    }

    public Stream<RoomEventDTO> after(String room, int lastKnownId) {
        return events.after(room, lastKnownId)
            // To keep a small memory footprint, we have to detach the loaded entities
            // in order to don't inflate the persistence context
            .map(this::detach)
            .map(RoomEvent::toDTO);
    }

    private RoomEvent detach(RoomEvent event) {
        em.detach(event);
        return event;
    }
}
