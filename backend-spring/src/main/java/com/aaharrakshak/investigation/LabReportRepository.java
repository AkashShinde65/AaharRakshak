package com.aaharrakshak.investigation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LabReportRepository extends JpaRepository<LabReport, Long> {

    boolean existsByReportNumber(String reportNumber);

    List<LabReport> findBySampleIdOrderByUploadedAtDesc(Long sampleId);

    Optional<LabReport> findFirstBySampleIdOrderByUploadedAtDesc(Long sampleId);

    Optional<LabReport> findFirstByReportNumberOrderByUploadedAtDesc(String reportNumber);

    Optional<LabReport> findFirstBySampleComplaintIdAndStatusOrderByPublishedAtDesc(
            Long complaintId,
            LabReportStatus status);

    List<LabReport> findByStatusOrderByPublishedAtDesc(LabReportStatus status);

    List<LabReport> findBySampleComplaintCompanyIdAndStatus(Long companyId, LabReportStatus status);
}
