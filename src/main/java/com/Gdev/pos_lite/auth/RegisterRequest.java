package com.Gdev.pos_lite.auth;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "DTO para RegisterRequest")
public class RegisterRequest {

    @Schema(description = "Campo email", example = "ejemplo")
    private String email;

    @Schema(description = "Campo password", example = "ejemplo")
    private String password;

    @Schema(description = "Campo adminInviteCode", example = "ejemplo")
    private String adminInviteCode;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getAdminInviteCode() {
        return adminInviteCode;
    }

    public void setAdminInviteCode(String adminInviteCode) {
        this.adminInviteCode = adminInviteCode;
    }
}
