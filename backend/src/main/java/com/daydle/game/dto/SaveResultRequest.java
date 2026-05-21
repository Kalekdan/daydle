package com.daydle.game.dto;

import jakarta.validation.constraints.NotBlank;

public record SaveResultRequest(@NotBlank String text, String gameKey) {
}
