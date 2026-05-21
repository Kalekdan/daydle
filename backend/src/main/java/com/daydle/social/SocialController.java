package com.daydle.social;

import com.daydle.social.dto.FeedItemDto;
import com.daydle.social.dto.FollowUserDto;
import java.util.List;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class SocialController {
    private final SocialService socialService;

    public SocialController(SocialService socialService) {
        this.socialService = socialService;
    }

    @PostMapping("/follows/{userId}")
    public void follow(Authentication authentication, @PathVariable Long userId) {
        Long me = (Long) authentication.getPrincipal();
        socialService.follow(me, userId);
    }

    @DeleteMapping("/follows/{userId}")
    public void unfollow(Authentication authentication, @PathVariable Long userId) {
        Long me = (Long) authentication.getPrincipal();
        socialService.unfollow(me, userId);
    }

    @GetMapping("/follows/me")
    public List<FollowUserDto> following(Authentication authentication) {
        Long me = (Long) authentication.getPrincipal();
        return socialService.following(me);
    }

    @GetMapping("/feed")
    public List<FeedItemDto> feed(
            Authentication authentication,
            @RequestParam(required = false) String game,
            @RequestParam(defaultValue = "50") int limit
    ) {
        Long me = (Long) authentication.getPrincipal();
        return socialService.feed(me, game, Math.min(limit, 100));
    }
}
