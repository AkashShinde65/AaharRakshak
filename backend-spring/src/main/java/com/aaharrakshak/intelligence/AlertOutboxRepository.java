package com.aaharrakshak.intelligence;

import java.time.Instant;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AlertOutboxRepository extends JpaRepository<AlertOutbox, Long> {

    List<AlertOutbox> findByStatusInAndNextAttemptAtBeforeOrderByCreatedAtAsc(
            List<AlertOutboxStatus> statuses,
            Instant now);

    List<AlertOutbox> findByUserIdOrderByCreatedAtDesc(Long userId);

    List<AlertOutbox> findAllByOrderByCreatedAtDesc();
}
