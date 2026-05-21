package com.daydle.common;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface GameResultRepository extends JpaRepository<GameResult, Long> {
    Optional<GameResult> findByUserIdAndGameIdAndPlayedOn(Long userId, Long gameId, LocalDate playedOn);

    List<GameResult> findByUserIdAndGameGameKeyAndPlayedOnBetweenOrderByPlayedOnAsc(
            Long userId,
            String gameKey,
            LocalDate from,
            LocalDate to
    );

    List<GameResult> findByUserIdAndPlayedOnBetweenOrderByPlayedOnAsc(Long userId, LocalDate from, LocalDate to);

    List<GameResult> findByGameGameKeyAndPlayedOnBetweenOrderByPlayedOnAsc(String gameKey, LocalDate from, LocalDate to);
}
