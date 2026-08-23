package com.aaharrakshak.complaint;

import static org.assertj.core.api.Assertions.assertThat;

import com.aaharrakshak.complaint.dto.DetectedFoodDetails;
import com.aaharrakshak.complaint.dto.PackagedFoodScanRequest;
import com.aaharrakshak.storage.FileMetadataRequest;
import org.junit.jupiter.api.Test;

class MockOcrAdapterTest {

    private final MockOcrAdapter adapter = new MockOcrAdapter();

    @Test
    void returnsDeterministicDevelopmentHints() {
        DetectedFoodDetails details = adapter.detectPackagedFoodDetails(new PackagedFoodScanRequest(
                "8901234567890",
                new FileMetadataRequest("demo/front.jpg", "front.jpg", "image/jpeg", 100L),
                null,
                null,
                null));

        assertThat(details.productName()).isEqualTo("Demo Turmeric Powder");
        assertThat(details.fssaiLicenceNumber()).isEqualTo("12345678901234");
        assertThat(details.uncertain()).isFalse();
    }

    @Test
    void marksUnclearImagesAsUncertainForUserCorrection() {
        DetectedFoodDetails details = adapter.detectPackagedFoodDetails(new PackagedFoodScanRequest(
                null,
                new FileMetadataRequest("demo/uncertain-front.jpg", "front.jpg", "image/jpeg", 100L),
                null,
                null,
                null));

        assertThat(details.uncertain()).isTrue();
        assertThat(details.confidence()).isLessThan(0.6);
    }
}
