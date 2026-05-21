package com.daydle.game;

import com.daydle.common.Game;
import com.daydle.common.GameResult;
import com.daydle.common.GameResultRepository;
import com.daydle.common.User;
import com.daydle.common.UserRepository;
import com.daydle.game.dto.CandidateDto;
import com.daydle.game.dto.GameResultDto;
import com.daydle.game.dto.ParsePreviewResponse;
import com.daydle.game.module.DetectionResult;
import com.daydle.game.module.GameModule;
import com.daydle.game.module.ParseResult;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class ParseService {
    private final List<GameModule> modules;
    private final GameService gameService;
    private final UserRepository userRepository;
    private final GameResultRepository gameResultRepository;
    private final ObjectMapper objectMapper;

    public ParseService(
            List<GameModule> modules,
            GameService gameService,
            UserRepository userRepository,
            GameResultRepository gameResultRepository,
            ObjectMapper objectMapper
    ) {
        this.modules = modules;
        this.gameService = gameService;
        this.userRepository = userRepository;
        this.gameResultRepository = gameResultRepository;
        this.objectMapper = objectMapper;
    }

    public ParsePreviewResponse preview(String text) {
        List<DetectionResult> detections = modules.stream()
                .map(module -> module.detect(text))
                .sorted(Comparator.comparingDouble(DetectionResult::confidence).reversed())
                .toList();

        if (detections.isEmpty() || detections.get(0).confidence() < 0.4) {
            throw new IllegalArgumentException("No supported game detected in pasted text");
        }

        DetectionResult top = detections.get(0);
        GameModule module = moduleByKey(top.gameKey());
        ParseResult parsed = module.parse(text);

        List<CandidateDto> candidates = detections.stream()
                .filter(d -> d.confidence() > 0.15)
                .map(d -> new CandidateDto(d.gameKey(), d.confidence()))
                .toList();

        return new ParsePreviewResponse(candidates, top.gameKey(), parsed.normalized());
    }

    public GameResultDto save(Long userId, String text, String selectedGameKey) {
        ParsePreviewResponse preview = preview(text);
        String finalGameKey = selectedGameKey == null || selectedGameKey.isBlank()
                ? preview.selectedGameKey()
                : selectedGameKey;

        GameModule module = moduleByKey(finalGameKey);
        ParseResult parsed = module.parse(text);

        User user = userRepository.findById(userId).orElseThrow(() -> new IllegalArgumentException("User not found"));
        Game game = gameService.getByKey(finalGameKey);

        GameResult result = gameResultRepository
                .findByUserIdAndGameIdAndPlayedOn(user.getId(), game.getId(), parsed.playedOn())
                .orElse(new GameResult());

        result.setUser(user);
        result.setGame(game);
        result.setPlayedOn(parsed.playedOn());
        result.setRawText(text);
        result.setConfidence(preview.candidates().stream()
                .filter(c -> c.gameKey().equals(finalGameKey))
                .findFirst()
                .map(CandidateDto::confidence)
                .orElse(0.8));

        try {
            result.setParsedJson(objectMapper.writeValueAsString(parsed.normalized()));
        } catch (Exception ex) {
            throw new IllegalArgumentException("Failed to serialize parsed result");
        }

        GameResult saved = gameResultRepository.save(result);
        return new GameResultDto(saved.getId(), finalGameKey, saved.getPlayedOn(), parsed.normalized());
    }

    public List<GameResultDto> myResults(Long userId, String gameKey, LocalDate from, LocalDate to) {
        List<GameResult> results = gameResultRepository.findByUserIdAndGameGameKeyAndPlayedOnBetweenOrderByPlayedOnAsc(
                userId,
                gameKey,
                from,
                to
        );

        return results.stream().map(this::toDto).toList();
    }

    public Map<String, Object> parseJson(String parsedJson) {
        try {
            return objectMapper.readValue(parsedJson, new TypeReference<>() {
            });
        } catch (Exception ex) {
            return Map.of();
        }
    }

    private GameResultDto toDto(GameResult result) {
        return new GameResultDto(result.getId(), result.getGame().getGameKey(), result.getPlayedOn(), parseJson(result.getParsedJson()));
    }

    private GameModule moduleByKey(String key) {
        return modules.stream()
                .filter(module -> module.key().equals(key))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("Unsupported game key"));
    }
}
