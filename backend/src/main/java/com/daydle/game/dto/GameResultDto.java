package com.daydle.game.dto;

import java.time.LocalDate;
import java.util.Map;

public record GameResultDto(Long id, String gameKey, LocalDate playedOn, Map<String, Object> parsed) {
}
