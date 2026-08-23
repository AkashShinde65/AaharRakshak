package com.aaharrakshak.complaint;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EvidenceRepository extends JpaRepository<Evidence, Long> {

    List<Evidence> findByComplaintIdOrderByUploadedAtAsc(Long complaintId);
}
