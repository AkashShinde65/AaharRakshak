package com.aaharrakshak.investigation;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ActionRepository extends JpaRepository<Action, Long> {

    boolean existsByActionNumber(String actionNumber);

    List<Action> findByComplaintIdOrderByDecidedAtDesc(Long complaintId);

    Optional<Action> findFirstByLabReportIdOrderByDecidedAtDesc(Long labReportId);

    List<Action> findByTypeInOrderByDecidedAtDesc(Collection<ActionType> actionTypes);

    long countByCompanyIdAndTypeIn(Long companyId, Collection<ActionType> actionTypes);
}
