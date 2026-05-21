package com.daydle.game.module;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class WordleModule extends AbstractModule {
    private static final Pattern SCORE = Pattern.compile("Wordle\\s+(\\d+)\\s+([1-6X])/6", Pattern.CASE_INSENSITIVE);

    @Override
    public String key() {
        return "wordle";
    }

    @Override
    public DetectionResult detect(String input) {
        String text = sanitize(input);
        double confidence = text.toLowerCase().contains("wordle") ? 0.6 : 0.0;
        Matcher matcher = SCORE.matcher(text);
        Map<String, Object> hints = new HashMap<>();
        if (matcher.find()) {
            confidence = 0.98;
            hints.put("puzzleNumber", Integer.parseInt(matcher.group(1)));
            hints.put("attempts", matcher.group(2));
        }
        return new DetectionResult(key(), confidence, hints);
    }

    @Override
    public ParseResult parse(String input) {
        String text = sanitize(input);
        Matcher matcher = SCORE.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not parse Wordle result");
        }
        String attemptsRaw = matcher.group(2);
        boolean won = !"X".equalsIgnoreCase(attemptsRaw);

        Map<String, Object> normalized = new HashMap<>();
        normalized.put("puzzleNumber", Integer.parseInt(matcher.group(1)));
        normalized.put("outcome", won ? "win" : "loss");
        normalized.put("attemptsUsed", won ? Integer.parseInt(attemptsRaw) : null);
        normalized.put("maxAttempts", 6);

        return new ParseResult(key(), LocalDate.now(), normalized);
    }
}
