package com.Gdev.pos_lite.auth;

import java.util.Set;

public class UserInfoResponse {
    private final String email;
    private final Set<String> roles;

    public UserInfoResponse(String email, Set<String> roles) {
        this.email = email;
        this.roles = roles;
    }

    public String getEmail() { return email; }
    public Set<String> getRoles() { return roles; }
}