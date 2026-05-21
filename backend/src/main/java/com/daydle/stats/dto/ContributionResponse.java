package com.daydle.stats.dto;

import java.util.List;

public record ContributionResponse(String gameKey, int year, List<ContributionDayDto> days) {
}
