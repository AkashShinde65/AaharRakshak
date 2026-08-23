package com.aaharrakshak.investigation;

import static org.assertj.core.api.Assertions.assertThatNoException;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.aaharrakshak.complaint.EvidenceType;
import com.aaharrakshak.investigation.dto.InvestigationFileMetadataRequest;
import com.aaharrakshak.investigation.dto.LabReportUploadRequest;
import com.aaharrakshak.investigation.dto.LabTestResultRequest;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

class InvestigationFileValidatorTest {

    private final InvestigationFileValidator validator = new InvestigationFileValidator();

    @Test
    void acceptsInspectionImagesAndSecurePdfReports() {
        assertThatNoException().isThrownBy(() -> validator.validateInspectionEvidence(new InvestigationFileMetadataRequest(
                EvidenceType.FOOD_PHOTO,
                "inspection/food.jpg",
                "food.jpg",
                "image/jpeg",
                1024L,
                "a".repeat(64),
                Instant.parse("2026-01-01T10:00:00Z"))));

        assertThatNoException().isThrownBy(() -> validator.validateLabReport(new LabReportUploadRequest(
                "LAB-UNIT-0001",
                "lab-reports/LAB-UNIT-0001.pdf",
                "LAB-UNIT-0001.pdf",
                "application/pdf",
                2048L,
                "b".repeat(64),
                "Within demo limits",
                LabOutcome.SAFE,
                List.of(new LabTestResultRequest(
                        "Moisture",
                        "Mock method",
                        "< 12%",
                        "8.2",
                        "%",
                        true,
                        "Demo result")))));
    }

    @Test
    void rejectsUnsafeInspectionAndReportFiles() {
        assertThatThrownBy(() -> validator.validateInspectionEvidence(new InvestigationFileMetadataRequest(
                EvidenceType.VIDEO,
                "../inspection/video.mov",
                "video.mov",
                "video/quicktime",
                1024L,
                "c".repeat(64),
                Instant.parse("2026-01-01T10:00:00Z"))))
                .isInstanceOf(ResponseStatusException.class);

        assertThatThrownBy(() -> validator.validateLabReport(new LabReportUploadRequest(
                "LAB-UNIT-0002",
                "lab-reports/LAB-UNIT-0002.png",
                "LAB-UNIT-0002.png",
                "image/png",
                2048L,
                "d".repeat(64),
                "Wrong content type",
                LabOutcome.INCONCLUSIVE,
                List.of(new LabTestResultRequest(
                        "Moisture",
                        "Mock method",
                        "< 12%",
                        "8.2",
                        "%",
                        true,
                        "Demo result")))))
                .isInstanceOf(ResponseStatusException.class);
    }
}
