package com.aaharrakshak.intelligence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SlaEscalationRepository extends JpaRepository<SlaEscalation, Long> {

    boolean existsByComplaintId(Long complaintId);

    List<SlaEscalation> findAllByOrderByEscalatedAtDesc();
}
