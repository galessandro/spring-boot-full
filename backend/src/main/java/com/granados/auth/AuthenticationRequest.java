package com.granados.auth;

public record AuthenticationRequest(
        String username,
        String password
) {
}
