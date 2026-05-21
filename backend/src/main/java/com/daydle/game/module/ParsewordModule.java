package com.daydle.game.module;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class ParsewordModule extends AbstractModule {
    private static final Pattern SCORE = Pattern.compile("parseword.*?(score|points?)[:\\s]+(\\d+)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public String key() {
        return "parseword";
    }

    @Override
    public DetectionResult detect(String input) {
        String text = sanitize(input);
        double confidence = text.toLowerCase().contains("parseword") ? 0.7 : 0.0;
        Map<String, Object> hints = new HashMap<>();
        Matcher matcher = SCORE.matcher(text);
        if (matcher.find()) {
            confidence = 0.94;
            hints.put("score", Integer.parseInt(matcher.group(2)));
        }
        return new DetectionResult(key(), confidence, hints);
    }

    @Override
    public ParseResult parse(String input) {
        String text = sanitize(input);
        Matcher matcher = SCORE.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not parse Parseword result");
        }

        int score = Integer.parseInt(matcher.group(2));
        Map<String, Object> normalized = new HashMap<>();
        normalized.put("score", score);
        normalized.put("outcome", "played");
        normalized.put("intensity", score >= 90 ? 4 : score >= 70 ? 3 : score >= 50 ? 2 : 1);

        return new ParseResult(key(), LocalDate.now(), normalized);
    }
}
