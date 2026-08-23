package com.aaharrakshak.intelligence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RiskAnalysisRepository extends JpaRepository<RiskAnalysis, Long> {

    List<RiskAnalysis> findByComplaintIdOrderByCreatedAtDesc(Long complaintId);
}
