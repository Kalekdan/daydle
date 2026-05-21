package com.daydle.auth.dto;

import com.daydle.common.Visibility;

public record UserDto(Long id, String username, String email, Visibility visibility) {
}
