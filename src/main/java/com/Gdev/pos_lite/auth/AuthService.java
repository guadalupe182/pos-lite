package com.Gdev.pos_lite.auth;

import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.user.Role;
import com.Gdev.pos_lite.user.User;
import com.Gdev.pos_lite.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final String adminInviteCode;

    public AuthService(UserRepository userRepository,
                       PasswordEncoder passwordEncoder,
                       JwtService jwtService,
                       @Value("${admin.invite.code:}") String adminInviteCode) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.adminInviteCode = adminInviteCode == null ? "" : adminInviteCode.trim();
    }

    public void register(RegisterRequest req) {
        String email = normalizeEmail(req.getEmail());
        String password = req.getPassword();

        if (email.isBlank()) throw new IllegalArgumentException("Email requerido");
        if (password == null || password.isBlank()) throw new IllegalArgumentException("Password requerido");
        if (userRepository.existsByEmailIgnoreCase(email))
            throw new IllegalArgumentException("Email ya registrado");

        User u = new User();
        u.setEmail(email);
        u.setPassword(passwordEncoder.encode(password));

        Set<Role> roles = Set.of(Role.USER);
        String invite = req.getAdminInviteCode() == null ? "" : req.getAdminInviteCode().trim();
        if (!adminInviteCode.isBlank() && invite.equals(adminInviteCode)) {
            roles = Set.of(Role.ADMIN);
        }
        u.setRoles(roles);
        userRepository.save(u);
    }

    public String loginAndIssueToken(LoginRequest req) {
        String email = normalizeEmail(req.getEmail());
        String password = req.getPassword();

        User u = userRepository.findByEmailIgnoreCase(email)
                .orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));

        if (!u.isEnabled()) throw new IllegalArgumentException("Usuario deshabilitado");
        if (password == null || !passwordEncoder.matches(password, u.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }

        // Construir claims para el token
        Map<String, Object> claims = Map.of(
                "roles", u.getRoles().stream().map(Role::name).collect(Collectors.toList())
        );

        return jwtService.generateToken(u.getEmail(), claims);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}