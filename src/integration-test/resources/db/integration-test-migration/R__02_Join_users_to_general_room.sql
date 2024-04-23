-- Subscribe the users to the general room
INSERT INTO live.room_membership (room_id, user_id) VALUES
((SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'ally_boy')),
((SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'tamara_spinazzola')),
((SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'jimmycarter')),
((SELECT id FROM live.room WHERE general is TRUE), (SELECT id FROM live.user WHERE login = 'lucasg'));

-- Subscribe the users to the legal room
INSERT INTO live.room_membership (room_id, user_id) VALUES
((SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'tamara_spinazzola')),
((SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'jimmycarter')),
((SELECT id FROM live.room WHERE handle = 'legal'), (SELECT id FROM live.user WHERE login = 'lucasg'))
