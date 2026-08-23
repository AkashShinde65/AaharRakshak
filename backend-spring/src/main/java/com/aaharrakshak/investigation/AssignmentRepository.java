package com.aaharrakshak.investigation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AssignmentRepository extends JpaRepository<Assignment, Long> {

    boolean existsByComplaintIdAndAssignedToId(Long complaintId, Long assignedToId);

    Optional<Assignment> findFirstByComplaintIdOrderByAssignedAtDesc(Long complaintId);

    List<Assignment> findByAssignedToIdOrderByAssignedAtDesc(Long assignedToId);
}
