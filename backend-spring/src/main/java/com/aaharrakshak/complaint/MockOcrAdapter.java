package com.aaharrakshak.complaint;

import com.aaharrakshak.complaint.dto.DetectedFoodDetails;
import com.aaharrakshak.complaint.dto.PackagedFoodScanRequest;
import java.time.LocalDate;
import org.springframework.stereotype.Service;

@Service
public class MockOcrAdapter implements OcrAdapter {

    @Override
    public DetectedFoodDetails detectPackagedFoodDetails(PackagedFoodScanRequest request) {
        String objectKeyHint = request.frontLabelImage() == null ? "" : request.frontLabelImage().objectKey();
        if (objectKeyHint != null && objectKeyHint.toLowerCase().contains("uncertain")) {
            return new DetectedFoodDetails(
                    "Uncertain Mock Product",
                    "Unclear Mock Company",
                    "12345678901234",
                    "UNKNOWN-BATCH",
                    LocalDate.now().plusMonths(6),
                    0.52,
                    true);
        }
        return new DetectedFoodDetails(
                "Demo Turmeric Powder",
                "Demo Foods Private Limited",
                "12345678901234",
                "TUR-2026-001",
                LocalDate.now().plusMonths(9),
                0.86,
                false);
    }
}
