package io.chat.live.outbox;

import io.chat.live.json.JsonSerializer;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;

@Repository
@RequiredArgsConstructor
public class OutboxRepository {

    private final NamedParameterJdbcTemplate jdbc;
    private final JsonSerializer serializer;

    private static final String INSERT = """
        INSERT INTO live.outbox_message (topic, event_id, data)
        VALUES (:topic, :event_id, :data);
        """;

    public void save(String topic, int eventId, Object event) {
        jdbc.update(INSERT, Map.of(
            "topic", topic,
            "event_id", eventId,
            "data", serializer.toJson(event)
        ));
    }

    public List<OutboxMessage> fetchPending(int count) {
        var query = """
            SELECT * FROM live.outbox_message
            WHERE status = 'PENDING'
            ORDER BY id, event_id LIMIT :count
            """;
        var args = Map.of("count", count);
        return jdbc.query(query, args, new OutboxMessageRowMapper());
    }

    public void removeAll(List<OutboxMessage> successful) {
        if (successful.isEmpty()) return;
        var ids = successful.stream()
            .map(OutboxMessage::getId)
            .toList();
        var delete = "DELETE FROM live.outbox_message WHERE id IN (:ids)";
        var args = new MapSqlParameterSource("ids", ids);
        jdbc.update(delete, args);
    }

    public void setFailedState(List<OutboxMessage> failed) {
        if (failed.isEmpty()) return;
        var ids = failed.stream()
            .map(OutboxMessage::getId)
            .toList();
        var delete = """
            UPDATE live.outbox_message SET status = 'ERROR'
            WHERE id IN (:ids)
            """;
        var args = new MapSqlParameterSource("ids", ids);
        jdbc.update(delete, args);
    }

    public void increaseAttempt(List<OutboxMessage> attempted) {
        if (attempted.isEmpty()) return;
        var ids = attempted.stream()
            .map(OutboxMessage::getId)
            .toList();
        var update = """
            UPDATE live.outbox_message SET attempts = attempts + 1
            WHERE id IN (:ids)
            """;
        var args = new MapSqlParameterSource("ids", ids);
        jdbc.update(update, args);
    }
}
