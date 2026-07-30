package com.Gdev.pos_lite.auth;

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

        // Cookie compatible
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(true)          // PROD: true
                .sameSite("None")      // si un día es cross-site real: None + secure true
                .path("/")
                .maxAge(Duration.ofMinutes(24 * 60)) // o usa expMinutes si quieres exacto
                .build();

        //devolver el token en el body para que el front lo guarde
        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE, cookie.toString())
                .body(Map.of("token", token));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout() {
        // 🎯 FIX: Para borrar la cookie, debe tener exactamente la misma configuración de seguridad que al crearla
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(true)          // Debe coincidir con login
                .sameSite("None")      // Debe coincidir con login
                .path("/")
                .maxAge(Duration.ZERO) // Esto es lo que la destruye
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

    // (Opcional) Manejo simple de errores para que no te regrese 500 feo
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}