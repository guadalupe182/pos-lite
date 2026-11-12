package com.Gdev.pos_lite.auth;

import com.Gdev.pos_lite.license.License;
import com.Gdev.pos_lite.license.LicenseRepository;
import com.Gdev.pos_lite.license.LicenseService;
import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.user.User;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import com.Gdev.pos_lite.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatIllegalArgumentException;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private JwtService jwtService;
    @Mock
    private LicenseService licenseService;

    @Mock
    private LicenseRepository licenseRepository;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, licenseService, licenseRepository, "");
    }

    @Test
    void registraElUsuarioCuandoElCrmValidaLaLicencia() {
        RegisterRequest request = request();
        when(licenseService.validateLicense("ABC-123")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-password");

        authService.register(request);

        verify(licenseService).validateLicense("ABC-123");
        org.mockito.ArgumentCaptor<User> userCaptor = org.mockito.ArgumentCaptor.forClass(User.class);
        verify(userRepository).save(userCaptor.capture());
        assertThat(userCaptor.getValue().getLicenseKey()).isEqualTo("ABC-123");
    }

    @Test
    void noCreaElUsuarioCuandoLaLicenciaEsInvalida() {
        RegisterRequest request = request();
        when(licenseService.validateLicense("ABC-123")).thenReturn(false);

        assertThatIllegalArgumentException()
                .isThrownBy(() -> authService.register(request))
                .withMessage("Licencia inválida");

        verify(userRepository, never()).save(any(User.class));
        verifyNoInteractions(passwordEncoder);
    }

    @Test
    void loginEmiteFeaturesYTenantParaLicenciaFit() {
        User user = userWithLicense("GDEV-FIT-2026-TEST");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        License license = license("GDEV-FIT-2026-TEST", "tenant-fit", "ACTIVE", LocalDate.now().plusDays(10));
        when(licenseRepository.findByLicenseKey("GDEV-FIT-2026-TEST")).thenReturn(Optional.of(license));

        authService.loginAndIssueToken(login());

        org.mockito.ArgumentCaptor<java.util.Map<String, Object>> captor =
                org.mockito.ArgumentCaptor.forClass(java.util.Map.class);
        verify(jwtService).generateToken(org.mockito.ArgumentMatchers.eq("user@example.com"), captor.capture());
        assertThat(captor.getValue().get("features")).isEqualTo(List.of("FIT"));
        assertThat(captor.getValue().get("tenant_id")).isEqualTo("tenant-fit");
    }

    @Test
    void loginEmiteFeaturesYTenantParaLicenciaStyle() {
        User user = userWithLicense("GDEV-STYLE-2026-TEST");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        License license = license("GDEV-STYLE-2026-TEST", "tenant-style", "ACTIVE", LocalDate.now().plusDays(10));
        when(licenseRepository.findByLicenseKey("GDEV-STYLE-2026-TEST")).thenReturn(Optional.of(license));

        authService.loginAndIssueToken(login());

        org.mockito.ArgumentCaptor<java.util.Map<String, Object>> captor =
                org.mockito.ArgumentCaptor.forClass(java.util.Map.class);
        verify(jwtService).generateToken(org.mockito.ArgumentMatchers.eq("user@example.com"), captor.capture());
        assertThat(captor.getValue().get("features")).isEqualTo(List.of("STYLE"));
        assertThat(captor.getValue().get("tenant_id")).isEqualTo("tenant-style");
    }

    @Test
    void loginSinFeaturesSiLicenciaInactivaOVencida() {
        User user = userWithLicense("GDEV-FIT-2026-TEST");
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);
        License license = license("GDEV-FIT-2026-TEST", "tenant-fit", "INACTIVE", LocalDate.now().plusDays(10));
        when(licenseRepository.findByLicenseKey("GDEV-FIT-2026-TEST")).thenReturn(Optional.of(license));

        authService.loginAndIssueToken(login());

        org.mockito.ArgumentCaptor<java.util.Map<String, Object>> captor =
                org.mockito.ArgumentCaptor.forClass(java.util.Map.class);
        verify(jwtService).generateToken(org.mockito.ArgumentMatchers.eq("user@example.com"), captor.capture());
        assertThat(captor.getValue()).doesNotContainKey("features");
        assertThat(captor.getValue()).doesNotContainKey("tenant_id");
    }

    @Test
    void loginSinFeaturesSiNoHayLicenseKey() {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRoles(java.util.Set.of(com.Gdev.pos_lite.user.Role.USER));
        when(userRepository.findByEmailIgnoreCase("user@example.com")).thenReturn(Optional.of(user));
        when(passwordEncoder.matches("secret", "encoded")).thenReturn(true);

        authService.loginAndIssueToken(login());

        org.mockito.ArgumentCaptor<java.util.Map<String, Object>> captor =
                org.mockito.ArgumentCaptor.forClass(java.util.Map.class);
        verify(jwtService).generateToken(org.mockito.ArgumentMatchers.eq("user@example.com"), captor.capture());
        assertThat(captor.getValue()).doesNotContainKey("features");
        assertThat(captor.getValue()).doesNotContainKey("tenant_id");
    }

    private User userWithLicense(String key) {
        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded");
        user.setEnabled(true);
        user.setRoles(java.util.Set.of(com.Gdev.pos_lite.user.Role.USER));
        user.setLicenseKey(key);
        return user;
    }

    private License license(String key, String tenant, String status, LocalDate expiration) {
        License l = new License();
        l.setLicenseKey(key);
        l.setTenantId(tenant);
        l.setStatus(status);
        l.setExpirationDate(expiration);
        return l;
    }

    private LoginRequest login() {
        LoginRequest r = new LoginRequest();
        r.setEmail("user@example.com");
        r.setPassword("secret");
        return r;
    }

    private RegisterRequest request() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");
        request.setLicenseKey("ABC-123");
        return request;
    }
}
