package com.daydle.game.module;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ConnectionsModule extends AbstractModule {
    private static final Pattern PUZZLE = Pattern.compile("Connections\\s*#?(\\d+)", Pattern.CASE_INSENSITIVE);
    private static final Pattern MISTAKES = Pattern.compile("(\\d+)\\s+mistakes?", Pattern.CASE_INSENSITIVE);

    @Override
    public String key() {
        return "connections";
    }

    @Override
    public DetectionResult detect(String input) {
        String text = sanitize(input);
        double confidence = text.toLowerCase().contains("connections") ? 0.75 : 0.0;
        Map<String, Object> hints = new HashMap<>();

        Matcher puzzle = PUZZLE.matcher(text);
        if (puzzle.find()) {
            confidence = 0.97;
            hints.put("puzzleNumber", Integer.parseInt(puzzle.group(1)));
        }
        return new DetectionResult(key(), confidence, hints);
    }

    @Override
    public ParseResult parse(String input) {
        String text = sanitize(input);
        Matcher puzzle = PUZZLE.matcher(text);
        if (!puzzle.find()) {
            throw new IllegalArgumentException("Could not parse Connections result");
        }

        int mistakes = 0;
        Matcher mistakesMatch = MISTAKES.matcher(text);
        if (mistakesMatch.find()) {
            mistakes = Integer.parseInt(mistakesMatch.group(1));
        }

        boolean solved = !text.toLowerCase().contains("failed") && mistakes < 4;

        Map<String, Object> normalized = new HashMap<>();
        normalized.put("puzzleNumber", Integer.parseInt(puzzle.group(1)));
        normalized.put("mistakes", mistakes);
        normalized.put("outcome", solved ? "win" : "loss");
        normalized.put("intensity", solved ? Math.max(1, 4 - mistakes) : 1);

        return new ParseResult(key(), LocalDate.now(), normalized);
    }
}
