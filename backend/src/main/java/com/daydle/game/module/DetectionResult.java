package com.daydle.game.module;

import java.util.Map;

public record DetectionResult(String gameKey, double confidence, Map<String, Object> hints) {
}
