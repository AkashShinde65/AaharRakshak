package com.aaharrakshak.investigation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionVisitRepository extends JpaRepository<InspectionVisit, Long> {

    List<InspectionVisit> findByComplaintIdOrderByScheduledAtDesc(Long complaintId);

    long countByComplaintCompanyIdAndStatus(Long companyId, InspectionVisitStatus status);
}
