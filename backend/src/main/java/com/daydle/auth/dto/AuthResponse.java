package com.daydle.auth.dto;

public record AuthResponse(Long id, String username, String email, String token) {
}
