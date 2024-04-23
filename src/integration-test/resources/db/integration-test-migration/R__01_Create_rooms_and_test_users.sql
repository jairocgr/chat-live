-- Create test rooms
INSERT INTO live.room (name, handle) VALUES ('Legal Department', 'legal');
INSERT INTO live.room_event (type, room, author)
    VALUES ('CREATE', (SELECT id FROM live.room WHERE handle = 'legal'), 1);

-- Create users with the password "p4ssword" (must be kept in sync with BaseIntegrationTest#TEST_USERS
INSERT INTO live.user (name, login, role, password) VALUES
('Albert Barese', 'ally_boy', 'USER', '$2y$10$8dUETLShzFnWcpTefqu8jOyfWsXwTselah2kvRAXzORDyCThgHI56'),
('Tomara Spinazzola', 'tamara_spinazzola', 'USER', '$2y$10$8dUETLShzFnWcpTefqu8jOyfWsXwTselah2kvRAXzORDyCThgHI56'),
('James Earl Carter', 'jimmycarter', 'USER', '$2y$10$8dUETLShzFnWcpTefqu8jOyfWsXwTselah2kvRAXzORDyCThgHI56'),
('Lucas Graciano', 'lucasg', 'USER', '$2y$10$8dUETLShzFnWcpTefqu8jOyfWsXwTselah2kvRAXzORDyCThgHI56');

