package com.aaharrakshak.investigation;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SampleLabAssignmentRepository extends JpaRepository<SampleLabAssignment, Long> {

    boolean existsBySampleIdAndAssignedToId(Long sampleId, Long assignedToId);

    Optional<SampleLabAssignment> findFirstBySampleIdOrderByAssignedAtDesc(Long sampleId);

    List<SampleLabAssignment> findByAssignedToIdOrderByAssignedAtDesc(Long assignedToId);
}
