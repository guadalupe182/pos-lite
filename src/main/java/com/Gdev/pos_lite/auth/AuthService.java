package com.Gdev.pos_lite.auth;

import com.Gdev.pos_lite.license.License;
import com.Gdev.pos_lite.license.LicenseRepository;
import com.Gdev.pos_lite.license.LicenseService;
import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.user.Role;
import com.Gdev.pos_lite.user.User;
import com.Gdev.pos_lite.user.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthService {

    private final UserRepository userRepository;

    private final PasswordEncoder passwordEncoder;

    private final JwtService jwtService;

    private final LicenseService licenseService;

    private final LicenseRepository licenseRepository;

    private final String adminInviteCode;

    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService,
                       LicenseService licenseService, LicenseRepository licenseRepository,
                       @Value("${admin.invite.code:}") String adminInviteCode) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.licenseService = licenseService;
        this.licenseRepository = licenseRepository;
        this.adminInviteCode = adminInviteCode == null ? "" : adminInviteCode.trim();
    }

    public void register(RegisterRequest req) {
        String email = normalizeEmail(req.getEmail());
        String password = req.getPassword();
        if (email.isBlank())
            throw new IllegalArgumentException("Email requerido");
        if (password == null || password.isBlank())
            throw new IllegalArgumentException("Password requerido");
        if (!licenseService.validateLicense(req.getLicenseKey()))
            throw new IllegalArgumentException("Licencia inválida");
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
        if (req.getLicenseKey() != null && !req.getLicenseKey().isBlank()) {
            u.setLicenseKey(req.getLicenseKey().trim());
        }
        userRepository.save(u);
    }

    public String loginAndIssueToken(LoginRequest req) {
        String email = normalizeEmail(req.getEmail());
        String password = req.getPassword();
        User u = userRepository.findByEmailIgnoreCase(email).orElseThrow(() -> new IllegalArgumentException("Credenciales inválidas"));
        if (!u.isEnabled())
            throw new IllegalArgumentException("Usuario deshabilitado");
        if (password == null || !passwordEncoder.matches(password, u.getPassword())) {
            throw new IllegalArgumentException("Credenciales inválidas");
        }
        // Construir claims para el token
        Map<String, Object> claims = new HashMap<>();
        claims.put("roles", u.getRoles().stream().map(Role::name).collect(Collectors.toList()));

        String licenseKey = u.getLicenseKey();
        if (licenseKey != null && !licenseKey.isBlank()) {
            licenseRepository.findByLicenseKey(licenseKey).ifPresent(license -> {
                boolean active = "ACTIVE".equalsIgnoreCase(license.getStatus());
                boolean notExpired = license.getExpirationDate() == null
                        || !license.getExpirationDate().isBefore(LocalDate.now());
                if (active && notExpired) {
                    List<String> features = new ArrayList<>();
                    String upperKey = licenseKey.toUpperCase();
                    if (upperKey.contains("FIT")) {
                        features.add("FIT");
                    }
                    if (upperKey.contains("STYLE")) {
                        features.add("STYLE");
                    }
                    if (!features.isEmpty()) {
                        claims.put("features", features);
                    }
                    if (license.getTenantId() != null && !license.getTenantId().isBlank()) {
                        claims.put("tenant_id", license.getTenantId());
                    }
                }
            });
        }

        return jwtService.generateToken(u.getEmail(), claims);
    }

    private String normalizeEmail(String email) {
        return email == null ? "" : email.trim().toLowerCase();
    }
}
