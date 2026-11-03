package com.Gdev.pos_lite.auth;

import com.Gdev.pos_lite.license.LicenseService;
import com.Gdev.pos_lite.security.JwtService;
import com.Gdev.pos_lite.security.SecurityConfig;
import com.Gdev.pos_lite.user.UserRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(controllers = AuthController.class)
@Import({SecurityConfig.class, AuthService.class})
@TestPropertySource(properties = "app.cors.allowed-origins=http://localhost:3000")
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtService jwtService;

    @MockBean
    private LicenseService licenseService;

    @Test
    void registraCorrectamenteConLicenciaValida() throws Exception {
        when(licenseService.validateLicense("valid-key")).thenReturn(true);
        when(userRepository.existsByEmailIgnoreCase("user@example.com")).thenReturn(false);
        when(passwordEncoder.encode("secret")).thenReturn("encoded-password");

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"secret\",\"licenseKey\":\"valid-key\"}"))
                .andExpect(status().isCreated());

        verify(licenseService).validateLicense("valid-key");
        verify(userRepository).save(any());
    }

    @Test
    void rechazaElRegistroConLicenciaInvalida() throws Exception {
        when(licenseService.validateLicense("invalid-key")).thenReturn(false);

        mockMvc.perform(post("/api/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"email\":\"user@example.com\",\"password\":\"secret\",\"licenseKey\":\"invalid-key\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Licencia inválida"));

        verify(userRepository, never()).save(any());
    }
}
