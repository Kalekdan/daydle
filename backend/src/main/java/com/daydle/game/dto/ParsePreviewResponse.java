package com.daydle.game.dto;

import java.util.List;
import java.util.Map;

public record ParsePreviewResponse(
        List<CandidateDto> candidates,
        String selectedGameKey,
        Map<String, Object> normalized
) {
}
