package com.daydle.config;

import com.daydle.common.Game;
import com.daydle.common.GameRepository;
import jakarta.annotation.PostConstruct;
import java.util.List;
import org.springframework.stereotype.Component;

@Component
public class DataSeeder {
    private final GameRepository gameRepository;

    public DataSeeder(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }

    @PostConstruct
    public void seedGames() {
        if (gameRepository.count() > 0) {
            return;
        }
        gameRepository.saveAll(List.of(
                create("wordle", "Wordle"),
                create("maptap", "Maptap"),
                create("connections", "Connections"),
                create("parseword", "Parseword")
        ));
    }

    private Game create(String key, String name) {
        Game game = new Game();
        game.setGameKey(key);
        game.setDisplayName(name);
        game.setEnabled(true);
        return game;
    }
}
