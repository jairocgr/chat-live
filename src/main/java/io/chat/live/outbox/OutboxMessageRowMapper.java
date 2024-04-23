package io.chat.live.outbox;

import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class OutboxMessageRowMapper implements RowMapper<OutboxMessage> {
    @Override
    public OutboxMessage mapRow(ResultSet rs, int rowNum) throws SQLException {
        return OutboxMessage.builder()
            .id(rs.getInt("id"))
            .status(OutboxMessage.Status.valueOf(rs.getString("status")))
            .topic(rs.getString("topic"))
            .eventId(rs.getInt("event_id"))
            .data(rs.getString("data"))
            .attempts(rs.getInt("attempts"))
            .createdAt(rs.getTimestamp("created_at").toInstant())
            .build();
    }
}
