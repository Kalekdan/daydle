package com.daydle.game;

import com.daydle.common.Game;
import com.daydle.common.GameRepository;
import com.daydle.game.dto.GameDto;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class GameService {
    private final GameRepository gameRepository;

    public GameService(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    public List<GameDto> list() {
        return gameRepository.findByEnabledTrueOrderByDisplayNameAsc()
                .stream()
                .map(game -> new GameDto(game.getGameKey(), game.getDisplayName()))
                .toList();
    }

    public Game getByKey(String key) {
        return gameRepository.findByGameKey(key).orElseThrow(() -> new IllegalArgumentException("Unknown game key"));
    }
}
