package io.chat.live.service;

import io.chat.live.domain.NewUser;
import io.chat.live.dto.UserDTO;
import io.chat.live.exception.MaxUsersAllowedException;
import io.chat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository users;
    private final RoomService roomService;
    private final PasswordEncoder encoder;

    @Value("${app.user.max-users-allowed}")
    private final int maxUsersAllowed;

    @Transactional
    public UserDTO add(NewUser newUser) {

        if (users.userLimitReached(maxUsersAllowed)) {
            throw new MaxUsersAllowedException(maxUsersAllowed);
        }

        var user = newUser.toUser(encoder);
        users.save(user);
        roomService.joinGeneralRoom(user);
        return user.toDTO();
    }
}
