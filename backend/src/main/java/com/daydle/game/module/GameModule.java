package com.daydle.game.module;

public interface GameModule {
    String key();

    DetectionResult detect(String input);

    ParseResult parse(String input);
}
