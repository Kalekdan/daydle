package com.daydle.game;

import com.daydle.game.dto.GameDto;
import com.daydle.game.dto.GameResultDto;
import com.daydle.game.dto.ParsePreviewRequest;
import com.daydle.game.dto.ParsePreviewResponse;
import com.daydle.game.dto.SaveResultRequest;
import jakarta.validation.Valid;
import java.time.LocalDate;
import java.util.List;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class GameController {
    private final GameService gameService;
    private final ParseService parseService;

    public GameController(GameService gameService, ParseService parseService) {
        this.gameService = gameService;
        this.parseService = parseService;
    }

    @GetMapping("/games")
    public List<GameDto> games() {
        return gameService.list();
    }

    @PostMapping("/results/parse-preview")
    public ParsePreviewResponse parsePreview(@Valid @RequestBody ParsePreviewRequest request) {
        return parseService.preview(request.text());
    }

    @PostMapping("/results")
    public GameResultDto saveResult(Authentication authentication, @Valid @RequestBody SaveResultRequest request) {
        Long userId = (Long) authentication.getPrincipal();
        return parseService.save(userId, request.text(), request.gameKey());
    }

    @GetMapping("/results/me")
    public List<GameResultDto> myResults(
            Authentication authentication,
            @RequestParam String game,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to
    ) {
        Long userId = (Long) authentication.getPrincipal();
        return parseService.myResults(userId, game, from, to);
    }
}
