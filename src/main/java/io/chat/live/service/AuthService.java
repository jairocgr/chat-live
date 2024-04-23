package io.chat.live.service;

import io.chat.live.domain.User;
import io.chat.live.domain.UserCredential;
import io.chat.live.dto.UserFullDTO;
import io.chat.live.exception.InvalidCredentialException;
import io.chat.live.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository users;
    private final PasswordEncoder encoder;

    public UserFullDTO auth(UserCredential credential) {
        var login = credential.getLogin();
        var user = users.findByLogin(login).orElseThrow(InvalidCredentialException::new);
        if (verify(user, credential)) {
            return user.toFullDTO();
        } else {
            throw new InvalidCredentialException();
        }
    }

    private boolean verify(User user, UserCredential credential) {
        var passwd = credential.getPassword();
        var hash = user.getPassword();
        return encoder.matches(passwd, hash);
    }
}
