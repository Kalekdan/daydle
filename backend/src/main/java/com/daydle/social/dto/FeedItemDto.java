package com.daydle.social.dto;

import java.time.Instant;
import java.time.LocalDate;
import java.util.Map;

public record FeedItemDto(
        Long resultId,
        Long userId,
        String username,
        String gameKey,
        LocalDate playedOn,
        Instant submittedAt,
        Map<String, Object> parsed
) {
}
