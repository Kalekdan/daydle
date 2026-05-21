package com.daydle.game.module;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

@Component
public class MaptapModule extends AbstractModule {
    private static final Pattern SCORE = Pattern.compile("maptap.*?(\\d{1,5})\\s*(km|m)", Pattern.CASE_INSENSITIVE | Pattern.DOTALL);

    @Override
    public String key() {
        return "maptap";
    }

    @Override
    public DetectionResult detect(String input) {
        String text = sanitize(input);
        double confidence = text.toLowerCase().contains("maptap") ? 0.7 : 0.0;
        Map<String, Object> hints = new HashMap<>();
        Matcher matcher = SCORE.matcher(text);
        if (matcher.find()) {
            confidence = 0.95;
            hints.put("distance", Integer.parseInt(matcher.group(1)));
            hints.put("unit", matcher.group(2).toLowerCase());
        }
        return new DetectionResult(key(), confidence, hints);
    }

    @Override
    public ParseResult parse(String input) {
        String text = sanitize(input);
        Matcher matcher = SCORE.matcher(text);
        if (!matcher.find()) {
            throw new IllegalArgumentException("Could not parse Maptap result");
        }

        int distance = Integer.parseInt(matcher.group(1));
        String unit = matcher.group(2).toLowerCase();
        int meters = "km".equals(unit) ? distance * 1000 : distance;

        Map<String, Object> normalized = new HashMap<>();
        normalized.put("distanceMeters", meters);
        normalized.put("unit", unit);
        normalized.put("outcome", "played");
        normalized.put("intensity", meters < 1000 ? 4 : meters < 5000 ? 3 : meters < 10000 ? 2 : 1);

        return new ParseResult(key(), LocalDate.now(), normalized);
    }
}
