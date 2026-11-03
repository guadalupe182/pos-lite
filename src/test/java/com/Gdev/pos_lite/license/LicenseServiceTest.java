package com.Gdev.pos_lite.license;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

@ExtendWith(MockitoExtension.class)
class LicenseServiceTest {

    private static final String VALIDATION_URL = "https://api.gdevsoftware.com/api/v1/licenses/validate";

    @Mock
    private RestTemplateBuilder restTemplateBuilder;

    private MockRestServiceServer server;
    private LicenseService licenseService;

    @BeforeEach
    void setUp() {
        RestTemplate restTemplate = new RestTemplate();
        server = MockRestServiceServer.createServer(restTemplate);
        when(restTemplateBuilder.connectTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.readTimeout(any(Duration.class))).thenReturn(restTemplateBuilder);
        when(restTemplateBuilder.build()).thenReturn(restTemplate);
        licenseService = new LicenseService(restTemplateBuilder, VALIDATION_URL);
    }

    @Test
    void consultaElCrmYAceptaUnaLicenciaValida() {
        server.expect(requestTo(VALIDATION_URL + "/ABC-123"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("{\"valid\":true}", MediaType.APPLICATION_JSON));

        assertThat(licenseService.validateLicense("ABC-123")).isTrue();
        server.verify();
    }

    @Test
    void rechazaUnaLicenciaInvalida() {
        server.expect(requestTo(VALIDATION_URL + "/ABC-123"))
                .andRespond(withSuccess("{\"valid\":false}", MediaType.APPLICATION_JSON));

        assertThat(licenseService.validateLicense("ABC-123")).isFalse();
        server.verify();
    }

    @Test
    void noConsultaElCrmSiLaClaveEstaVacia() {
        assertThat(licenseService.validateLicense(" ")).isFalse();
        server.verify();
    }

    @Test
    void rechazaLaLicenciaCuandoElCrmRespondeConErrorDeCliente() {
        server.expect(requestTo(VALIDATION_URL + "/ABC-123"))
                .andRespond(withStatus(HttpStatus.NOT_FOUND));

        assertThat(licenseService.validateLicense("ABC-123")).isFalse();
        server.verify();
    }

    @Test
    void informaIndisponibilidadCuandoFallaElCrm() {
        server.expect(requestTo(VALIDATION_URL + "/ABC-123"))
                .andRespond(withStatus(HttpStatus.INTERNAL_SERVER_ERROR));

        assertThatThrownBy(() -> licenseService.validateLicense("ABC-123"))
                .isInstanceOf(ResponseStatusException.class)
                .extracting(ex -> ((ResponseStatusException) ex).getStatusCode())
                .isEqualTo(HttpStatus.SERVICE_UNAVAILABLE);
        server.verify();
    }
}
