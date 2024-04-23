package io.chat.live.controller;

import io.chat.live.domain.UserCredential;
import io.chat.live.dto.UserFullDTO;
import io.chat.live.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
public class LoginController {

    private final AuthService service;

    @PostMapping("/login")
    public UserFullDTO login(@RequestBody @Valid UserCredential credential) {
        return service.auth(credential);
    }
}
