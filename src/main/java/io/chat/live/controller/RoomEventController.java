package io.chat.live.controller;

import io.chat.live.domain.Message;
import io.chat.live.dto.RoomEventDTO;
import io.chat.live.event.NewMessage;
import io.chat.live.json.JsonSerializer;
import io.chat.live.kafka.RoomEventTopicReader;
import io.chat.live.repository.RoomRepository;
import io.chat.live.service.NewMessagePublisher;
import io.chat.live.service.RoomEventQuery;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;

import static java.lang.String.valueOf;

@RestController
@RequiredArgsConstructor
@Log4j2
public class RoomEventController {

    private final JsonSerializer serializer;
    private final KafkaProperties properties;
    private final ExecutorService executor;
    private final RoomRepository roomRepository;
    private final RoomEventQuery query;
    private final NewMessagePublisher publisher;

    @PostMapping("/room/{id}/messages")
    public void add(@PathVariable(name = "id") String room, @RequestBody @Valid Message message, @AuthenticationPrincipal UserDetails details) {
        var login = details.getUsername();
        var newMessage = NewMessage.builder()
            .room(room)
            .author(login)
            .message(message)
            .build();

        publisher.publish(newMessage);
    }

    @GetMapping("/room/{id}/events/before")
    public List<RoomEventDTO> before(@PathVariable(name = "id") String room, @RequestParam(name = "oldestId") int oldestId) {
        return query.before(room, oldestId);
    }

    @GetMapping("/room/{id}/events/after")
    @Transactional(readOnly = true)
    public Iterator<RoomEventDTO> after(@PathVariable(name = "id") String room, @RequestParam(name = "lastKnownId") int lastKnownId)  {
        return query.after(room, lastKnownId)
            .iterator();
    }

    @GetMapping(path = "/rooms/stream")
    public SseEmitter stream(@RequestParam(name = "room") List<String> rooms, @RequestParam(name = "prefetch", defaultValue = "64") int prefetch) {
        var reader = RoomEventTopicReader.builder()
            .properties(properties)
            .serializer(serializer)
            .repo(roomRepository)
            .build();
        SseEmitter emitter = new SseEmitter(Long.MAX_VALUE);
        executor.execute(() -> reader.read(rooms, prefetch)
            .map(event -> SseEmitter.event()
                .data(serializer.toJson(event))
                .id(valueOf(event.getId()))
                .name(event.getType().toString()))
            .forEach((event) -> {
                try {
                    emitter.send(event);
                } catch (IOException e) {
                    // As far as I could gather, this isn't a noteworthy exception. This usually
                    // happens when we try to send an event to an already closed connection
                    reader.stop();
                } catch (Exception e) {
                    log.error("Error sending SSE events", e);
                    emitter.completeWithError(e);
                    reader.stop();
                    throw e;
                }
            }));
        return emitter;
    }
}

