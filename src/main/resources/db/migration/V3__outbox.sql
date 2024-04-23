
CREATE TYPE live."outbox_message_status"
    AS ENUM('PENDING', 'ERROR');

CREATE TABLE IF NOT EXISTS live."outbox_message" (
    id SERIAL,
    status live.outbox_message_status NOT NULL DEFAULT 'PENDING',
    topic VARCHAR(128) NOT NULL,
    event_id INT,
    data TEXT NOT NULL,
    attempts INT NOT NULL DEFAULT 0,
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

-- WARN: Maybe this is a useless premature optimization
CREATE INDEX IF NOT EXISTS outbox_message_status_index
    ON live.outbox_message(status);
