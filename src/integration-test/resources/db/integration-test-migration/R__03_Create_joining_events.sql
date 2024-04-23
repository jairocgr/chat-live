-- Create the join event to the general room
INSERT INTO live.room_event (type, room, author) VALUES
('JOIN', (SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'ally_boy')),
('JOIN', (SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'tamara_spinazzola')),
('JOIN', (SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'jimmycarter')),
('JOIN', (SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'lucasg'));

-- Create the joining event to the legal room
INSERT INTO live.room_event (type, room, author) VALUES
('JOIN', (SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'tamara_spinazzola')),
('JOIN', (SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'jimmycarter')),
('JOIN', (SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'lucasg'));
