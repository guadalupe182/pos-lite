package com.Gdev.pos_lite.auth;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.web.bind.annotation.*;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthService authService;

    @Value("${spring.profiles.active:}")
    private String activeProfile;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterRequest req) {
        authService.register(req);
        return ResponseEntity.status(201).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest req) {
        String token = authService.loginAndIssueToken(req);

        boolean isDev = "dev".equalsIgnoreCase(activeProfile);
        boolean isSecure = !isDev;      // true solo en producción (no dev)
        String sameSite = isDev ? "Lax" : "None";

        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)   //para pruebas usar isSecure
                .sameSite("None") //para pruebas usar sameSite
                .path("/")
                .maxAge(Duration.ofMinutes(24 * 60))
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        boolean isDev = "dev".equalsIgnoreCase(activeProfile);
        boolean isSecure = !isDev;
        String sameSite = isDev ? "Lax" : "None";

        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(isSecure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ZERO)
                .build();

        return ResponseEntity.noContent()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .build();
    }

    @GetMapping("/me")
    public ResponseEntity<?> me(Authentication auth) {
        if (!(auth instanceof JwtAuthenticationToken jwtAuth)) {
            return ResponseEntity.status(401).build();
        }
        String email = jwtAuth.getToken().getSubject();
        Object rolesClaim = jwtAuth.getToken().getClaims().get("roles");

        Set<String> roles = new HashSet<>();
        if (rolesClaim instanceof List){
            for (Object role : (List<?>) rolesClaim){
                roles.add(role.toString());
            }
        } else if (rolesClaim instanceof String) {
            roles.add((String) rolesClaim);
        }

        return ResponseEntity.ok(Map.of(
                "email", email,
                "roles", roles
        ));
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}