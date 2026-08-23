package com.aaharrakshak.investigation;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface InspectionEvidenceRepository extends JpaRepository<InspectionEvidence, Long> {

    List<InspectionEvidence> findByInspectionVisitIdOrderByUploadedAtAsc(Long inspectionVisitId);
}
