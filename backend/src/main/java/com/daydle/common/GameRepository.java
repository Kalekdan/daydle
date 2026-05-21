package com.daydle.common;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameRepository extends JpaRepository<Game, Long> {
    Optional<Game> findByGameKey(String gameKey);

    List<Game> findByEnabledTrueOrderByDisplayNameAsc();
}
