package com.daydle.social;

import com.daydle.common.Follow;
import com.daydle.common.FollowRepository;
import com.daydle.common.GameResult;
import com.daydle.common.GameResultRepository;
import com.daydle.common.User;
import com.daydle.common.UserRepository;
import com.daydle.game.ParseService;
import com.daydle.social.dto.FeedItemDto;
import com.daydle.social.dto.FollowUserDto;
import java.util.Comparator;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class SocialService {
    private final FollowRepository followRepository;
    private final UserRepository userRepository;
    private final GameResultRepository gameResultRepository;
    private final ParseService parseService;

    public SocialService(
            FollowRepository followRepository,
            UserRepository userRepository,
            GameResultRepository gameResultRepository,
            ParseService parseService
    ) {
        this.followRepository = followRepository;
        this.userRepository = userRepository;
        this.gameResultRepository = gameResultRepository;
        this.parseService = parseService;
    }

    public void follow(Long followerId, Long followeeId) {
        if (followerId.equals(followeeId)) {
            throw new IllegalArgumentException("You cannot follow yourself");
        }
        if (followRepository.existsByFollowerIdAndFolloweeId(followerId, followeeId)) {
            return;
        }

        User follower = userRepository.findById(followerId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        User followee = userRepository.findById(followeeId).orElseThrow(() -> new IllegalArgumentException("User not found"));

        Follow follow = new Follow();
        follow.setFollower(follower);
        follow.setFollowee(followee);
        followRepository.save(follow);
    }

    public void unfollow(Long followerId, Long followeeId) {
        followRepository.findByFollowerIdAndFolloweeId(followerId, followeeId).ifPresent(followRepository::delete);
    }

    public List<FollowUserDto> following(Long userId) {
        return followRepository.findByFollowerId(userId)
                .stream()
                .map(f -> new FollowUserDto(f.getFollowee().getId(), f.getFollowee().getUsername()))
                .toList();
    }

    public List<FeedItemDto> feed(Long userId, String gameKey, int limit) {
        List<Long> followedIds = followRepository.findByFollowerId(userId).stream().map(f -> f.getFollowee().getId()).toList();
        if (followedIds.isEmpty()) {
            return List.of();
        }

        List<GameResult> rows = gameResultRepository.findAll().stream()
                .filter(r -> followedIds.contains(r.getUser().getId()))
                .filter(r -> gameKey == null || gameKey.isBlank() || r.getGame().getGameKey().equals(gameKey))
                .sorted(Comparator.comparing(GameResult::getPlayedOn).reversed().thenComparing(GameResult::getCreatedAt).reversed())
                .limit(limit)
                .toList();

        return rows.stream().map(r -> new FeedItemDto(
                r.getId(),
                r.getUser().getId(),
                r.getUser().getUsername(),
                r.getGame().getGameKey(),
                r.getPlayedOn(),
                r.getCreatedAt(),
                parseService.parseJson(r.getParsedJson())
        )).toList();
    }
}
