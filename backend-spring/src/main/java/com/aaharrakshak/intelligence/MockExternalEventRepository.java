package com.aaharrakshak.intelligence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MockExternalEventRepository extends JpaRepository<MockExternalEvent, Long> {

    List<MockExternalEvent> findAllByOrderByCreatedAtDesc();
}
