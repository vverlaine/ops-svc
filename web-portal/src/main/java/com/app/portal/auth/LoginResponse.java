package com.app.portal.auth;

public record LoginResponse(String token, String role, String name) {}