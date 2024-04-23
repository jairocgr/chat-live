
-- Create user admin, password "admin"
INSERT INTO live.user (name, login, role, password)
    VALUES ('Server Admin', 'admin', 'ADMIN', '$2y$10$80KAjJ77X6bRt2qDbHMF3.U34y8O/8Ky3iJnw.n/qOhk1Un3.8Zpq');

-- Create the general room and subscribe the admin to it
INSERT INTO live.room (name, handle, general) VALUES ('General Room', 'general', true);
INSERT INTO live.room_membership (room_id, user_id) VALUES (1, 1);
INSERT INTO live.room_event (type, room, author) VALUES ('CREATE', 1, 1);
INSERT INTO live.room_event (type, room, author) VALUES ('JOIN', 1, 1);
