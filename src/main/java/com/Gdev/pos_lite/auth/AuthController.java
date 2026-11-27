package com.Gdev.pos_lite.auth;

import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import jakarta.servlet.http.HttpServletRequest;
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
    public ResponseEntity<?> login(@RequestBody LoginRequest req, HttpServletRequest request) {
        String token = authService.loginAndIssueToken(req);
        // Cookie HttpOnly. Atributos adaptados al entorno:
        // - HTTPS (prod): Secure + SameSite=None (cross-site frontend/backend)
        // - HTTP (dev): Secure=false + SameSite=Lax (navegadores rechazan Secure en http)
        boolean secure = request.isSecure();
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie cookie = ResponseCookie.from("access_token", token)
                .httpOnly(true)
                .secure(secure)
                .sameSite(sameSite)
                .path("/")
                .maxAge(Duration.ofMinutes(24 * 60))
                .build();
        // No se devuelve el token en el body: el flujo es por cookie HttpOnly.
        return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString()).body(Map.of("message", "Login exitoso"));
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest request) {
        boolean secure = request.isSecure();
        String sameSite = secure ? "None" : "Lax";
        ResponseCookie cookie = ResponseCookie.from("access_token", "")
                .httpOnly(true)
                .secure(secure)
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
        if (rolesClaim instanceof List) {
            for (Object role : (List<?>) rolesClaim) {
                roles.add(role.toString());
            }
        } else if (rolesClaim instanceof String) {
            roles.add((String) rolesClaim);
        }
        return ResponseEntity.ok(Map.of("email", email, "roles", roles));
    }

    // (Opcional) Manejo simple de errores para que no te regrese 500 feo
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<?> badRequest(IllegalArgumentException e) {
        return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
    }
}
