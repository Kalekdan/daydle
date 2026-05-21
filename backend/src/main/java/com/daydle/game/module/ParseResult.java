package com.daydle.game.module;

import java.time.LocalDate;
import java.util.Map;

public record ParseResult(String gameKey, LocalDate playedOn, Map<String, Object> normalized) {
}
