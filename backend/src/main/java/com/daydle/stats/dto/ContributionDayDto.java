package com.daydle.stats.dto;

import java.time.LocalDate;

public record ContributionDayDto(LocalDate date, int intensity, boolean played) {
}
