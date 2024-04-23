package io.chat.live.controller;

import io.chat.live.domain.NewRoom;
import io.chat.live.dto.RoomDTO;
import io.chat.live.dto.RoomFullDTO;
import io.chat.live.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class RoomController {

    private final RoomService service;

    @PostMapping("/rooms")
    public RoomDTO add(@RequestBody @Valid NewRoom newRoom, @AuthenticationPrincipal UserDetails details) {
        var login = details.getUsername();
        return service.create(login, newRoom);
    }

    @GetMapping("/rooms")
    public List<RoomFullDTO> list() {
        return service.all();
    }

    @PostMapping("/room/{id}/join")
    public void join(@PathVariable(name = "id") String room, @AuthenticationPrincipal UserDetails details) {
        var login = details.getUsername();
        service.join(login, room);
    }

}
