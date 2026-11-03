package com.Gdev.pos_lite.license;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import java.time.Duration;

@Service
public class LicenseService {

    private final RestTemplate restTemplate;
    private final String validationUrl;

    public LicenseService(
            RestTemplateBuilder restTemplateBuilder,
            @Value("${crm.license.validation-url:https://api.gdevsoftware.com/api/v1/licenses/validate}")
            String validationUrl) {
        this.restTemplate = restTemplateBuilder
                .connectTimeout(Duration.ofSeconds(5))
                .readTimeout(Duration.ofSeconds(5))
                .build();
        this.validationUrl = validationUrl;
    }

    public boolean validateLicense(String licenseKey) {
        if (licenseKey == null || licenseKey.isBlank()) {
            return false;
        }

        try {
            LicenseValidationResponse response = restTemplate.getForObject(
                    validationUrl + "/{key}", LicenseValidationResponse.class, licenseKey.trim());
            return response != null && response.valid();
        } catch (HttpClientErrorException ex) {
            return false;
        } catch (RestClientException ex) {
            throw new ResponseStatusException(
                    HttpStatus.SERVICE_UNAVAILABLE,
                    "No se pudo validar la licencia con el CRM",
                    ex);
        }
    }
}
