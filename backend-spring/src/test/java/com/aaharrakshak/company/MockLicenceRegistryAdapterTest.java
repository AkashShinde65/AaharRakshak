package com.aaharrakshak.company;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;

class MockLicenceRegistryAdapterTest {

    private final MockLicenceRegistryAdapter adapter = new MockLicenceRegistryAdapter();

    @Test
    void verifiesValidLookingDemoLicenceNumbers() {
        RegistryLicenceDetails details = adapter.lookup("12345678901234");

        assertThat(details.verified()).isTrue();
        assertThat(details.status()).isEqualTo("VALID");
        assertThat(details.referenceToken()).startsWith("mock-fssai-");
    }

    @Test
    void rejectsMockNotFoundPatternWithoutExternalLookup() {
        RegistryLicenceDetails details = adapter.lookup("12345678900000");

        assertThat(details.verified()).isFalse();
        assertThat(details.status()).isEqualTo("NOT_FOUND");
    }
}
