package com.daydle.stats;

import com.daydle.stats.dto.ContributionResponse;
import com.daydle.stats.dto.OverviewDto;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/stats")
public class StatsController {
    private final StatsService statsService;

    public StatsController(StatsService statsService) {
        this.statsService = statsService;
    }

    @GetMapping("/me/contributions")
    public ContributionResponse myContributions(
            Authentication authentication,
            @RequestParam String game,
            @RequestParam int year
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return statsService.contributions(userId, game, year);
    }

    @GetMapping("/{userId}/contributions")
    public ContributionResponse userContributions(@PathVariable Long userId, @RequestParam String game, @RequestParam int year) {
        return statsService.contributions(userId, game, year);
    }

    @GetMapping("/me/overview")
    public OverviewDto myOverview(Authentication authentication) {
        Long userId = (Long) authentication.getPrincipal();
        return statsService.overview(userId);
    }
}
