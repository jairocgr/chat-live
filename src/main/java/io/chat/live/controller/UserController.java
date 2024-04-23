package io.chat.live.controller;

import io.chat.live.domain.NewUser;
import io.chat.live.dto.UserDTO;
import io.chat.live.service.UserService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class UserController {

    private final UserService service;

    @PostMapping("/users")
    public UserDTO add(@RequestBody @Valid NewUser newUser) {
        return service.add(newUser);
    }
}
