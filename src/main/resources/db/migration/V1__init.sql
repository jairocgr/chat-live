
CREATE TYPE live."user_role"
    AS ENUM('ADMIN', 'USER');

CREATE TABLE IF NOT EXISTS live."user" (
    id SERIAL,
    name VARCHAR(128) NOT NULL,
    login VARCHAR(32) NOT NULL UNIQUE,
    password VARCHAR(60) NOT NULL,
    role live.user_role NOT NULL DEFAULT 'USER',
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

CREATE TYPE live."room_type" AS ENUM('GROUP');

CREATE TABLE IF NOT EXISTS live."room" (
    id SERIAL,
    type live.room_type NOT NULL DEFAULT 'GROUP',
    name VARCHAR(128),
    handle VARCHAR(64) UNIQUE,
    general BOOLEAN NOT NULL DEFAULT false,
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY(id)
);

-- This insure only one general room
CREATE UNIQUE INDEX single_general_room_constraint
    ON live.room(general) WHERE (general);

CREATE TABLE IF NOT EXISTS live."room_membership" (
   room_id INT NOT NULL,
   user_id INT NOT NULL,
   PRIMARY KEY (room_id, user_id),
   CONSTRAINT fk_room FOREIGN KEY(room_id)
       REFERENCES room(id),
   CONSTRAINT fk_user FOREIGN KEY(user_id)
       REFERENCES live.user(id)
);

CREATE TYPE live."room_event_type" AS ENUM('CREATE', 'MESSAGE', 'JOIN');

CREATE TABLE IF NOT EXISTS live."room_event" (
    id SERIAL,
    type live.room_event_type NOT NULL DEFAULT 'MESSAGE',
    room INT NOT NULL,
    author INT NOT NULL,
    data jsonb,
    created_at timestamptz NOT NULL DEFAULT now(),
    PRIMARY KEY(id),
    CONSTRAINT fk_room FOREIGN KEY(room)
        REFERENCES room(id),
    CONSTRAINT fk_author FOREIGN KEY(author)
        REFERENCES live.user(id)
);

CREATE INDEX IF NOT EXISTS event_room_index ON live.room_event(room);

-- This insure only one CREATE event per room
CREATE UNIQUE INDEX single_create_event_per_room
    ON live.room_event(type, room) WHERE (type = 'CREATE');
