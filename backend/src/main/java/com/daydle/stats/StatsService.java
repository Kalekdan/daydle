package com.daydle.stats;

import com.daydle.common.GameResult;
import com.daydle.common.GameResultRepository;
import com.daydle.stats.dto.ContributionDayDto;
import com.daydle.stats.dto.ContributionResponse;
import com.daydle.stats.dto.OverviewDto;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class StatsService {
    private final GameResultRepository gameResultRepository;

    public StatsService(GameResultRepository gameResultRepository) {
        this.gameResultRepository = gameResultRepository;
    }

    public ContributionResponse contributions(Long userId, String gameKey, int year) {
        LocalDate from = LocalDate.of(year, 1, 1);
        LocalDate to = LocalDate.of(year, 12, 31);

        List<GameResult> results = gameResultRepository.findByUserIdAndGameGameKeyAndPlayedOnBetweenOrderByPlayedOnAsc(
                userId,
                gameKey,
                from,
                to
        );
        Map<LocalDate, Integer> intensityByDay = new HashMap<>();

        for (GameResult result : results) {
            int intensity = deriveIntensity(result.getParsedJson());
            intensityByDay.put(result.getPlayedOn(), intensity);
        }

        List<ContributionDayDto> days = new ArrayList<>();
        LocalDate cursor = from;
        while (!cursor.isAfter(to)) {
            int intensity = intensityByDay.getOrDefault(cursor, 0);
            days.add(new ContributionDayDto(cursor, intensity, intensity > 0));
            cursor = cursor.plusDays(1);
        }

        return new ContributionResponse(gameKey, year, days);
    }

    public OverviewDto overview(Long userId) {
        LocalDate to = LocalDate.now();
        LocalDate from = to.minusYears(2);
        List<GameResult> results = gameResultRepository.findByUserIdAndPlayedOnBetweenOrderByPlayedOnAsc(userId, from, to);

        int total = results.size();
        int currentStreak = 0;
        int longestStreak = 0;

        LocalDate expected = LocalDate.now();
        for (int i = results.size() - 1; i >= 0; i--) {
            LocalDate day = results.get(i).getPlayedOn();
            if (day.equals(expected)) {
                currentStreak++;
                expected = expected.minusDays(1);
            } else if (day.isBefore(expected)) {
                break;
            }
        }

        int running = 0;
        LocalDate prev = null;
        for (GameResult result : results) {
            LocalDate day = result.getPlayedOn();
            if (prev == null || day.equals(prev.plusDays(1))) {
                running++;
            } else if (!day.equals(prev)) {
                running = 1;
            }
            longestStreak = Math.max(longestStreak, running);
            prev = day;
        }

        return new OverviewDto(total, currentStreak, longestStreak);
    }

    private int deriveIntensity(String parsedJson) {
        if (parsedJson.contains("\"intensity\":4")) {
            return 4;
        }
        if (parsedJson.contains("\"intensity\":3")) {
            return 3;
        }
        if (parsedJson.contains("\"intensity\":2")) {
            return 2;
        }
        if (parsedJson.contains("\"attemptsUsed\":1") || parsedJson.contains("\"attemptsUsed\":2")) {
            return 4;
        }
        if (parsedJson.contains("\"attemptsUsed\":3") || parsedJson.contains("\"attemptsUsed\":4")) {
            return 3;
        }
        if (parsedJson.contains("\"attemptsUsed\":5") || parsedJson.contains("\"attemptsUsed\":6")) {
            return 2;
        }
        return 1;
    }
}
