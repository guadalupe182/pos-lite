package com.Gdev.pos_lite.auth;

import com.Gdev.pos_lite.license.LicenseService;
import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.user.User;
import com.Gdev.pos_lite.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

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

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, jwtService, licenseService, "");
    }

    @Test
    void registraElUsuarioCuandoElCrmValidaLaLicencia() {
        RegisterRequest request = request();
        when(licenseService.validateLicense("ABC-123")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-password");

        authService.register(request);

        verify(licenseService).validateLicense("ABC-123");
        verify(userRepository).save(any(User.class));
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

    private RegisterRequest request() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("user@example.com");
        request.setPassword("secret");
        request.setLicenseKey("ABC-123");
        return request;
    }
}
