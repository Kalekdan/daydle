package com.daydle.game.dto;

import jakarta.validation.constraints.NotBlank;

public record ParsePreviewRequest(@NotBlank String text) {
}
