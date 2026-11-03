package com.Gdev.pos_lite.license;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record LicenseValidationResponse(boolean valid) {
}
