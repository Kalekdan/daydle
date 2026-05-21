package com.daydle.auth;

import com.daydle.auth.dto.PublicUserDto;
import com.daydle.common.UserRepository;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/users")
public class UserController {
    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping
    public List<PublicUserDto> search(Authentication authentication, @RequestParam(defaultValue = "") String query) {
        Long me = (Long) authentication.getPrincipal();
        String normalized = query.toLowerCase();
        return userRepository.findAll().stream()
                .filter(u -> !u.getId().equals(me))
                .filter(u -> normalized.isBlank() || u.getUsername().toLowerCase().contains(normalized))
                .limit(25)
                .map(u -> new PublicUserDto(u.getId(), u.getUsername()))
                .toList();
    }
}
