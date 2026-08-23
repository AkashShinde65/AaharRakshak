package com.aaharrakshak.company;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class FssaiLicenceNumberValidatorTest {

    private final FssaiLicenceNumberValidator validator = new FssaiLicenceNumberValidator();

    @Test
    void acceptsExactlyFourteenDigits() {
        assertThat(validator.isValid("12345678901234", null)).isTrue();
    }

    @Test
    void rejectsMissingShortOrNonNumericValues() {
        assertThat(validator.isValid(null, null)).isFalse();
        assertThat(validator.isValid("1234567890123", null)).isFalse();
        assertThat(validator.isValid("1234567890123A", null)).isFalse();
    }
}
