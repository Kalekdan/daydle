package com.daydle.game.module;

import java.time.LocalDate;

public abstract class AbstractModule implements GameModule {
    protected LocalDate defaultPlayedOn() {
        return LocalDate.now();
    }

    protected String sanitize(String input) {
        return input == null ? "" : input.trim();
    }
}
