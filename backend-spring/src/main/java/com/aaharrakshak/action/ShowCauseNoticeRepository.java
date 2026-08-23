package com.aaharrakshak.action;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ShowCauseNoticeRepository extends JpaRepository<ShowCauseNotice, Long> {

    boolean existsByNoticeNumber(String noticeNumber);

    Optional<ShowCauseNotice> findByNoticeNumber(String noticeNumber);

    Optional<ShowCauseNotice> findFirstByLabReportIdOrderByIssuedAtDesc(Long labReportId);

    List<ShowCauseNotice> findByCompanyOwnerUserIdOrderByIssuedAtDesc(Long ownerUserId);

    List<ShowCauseNotice> findAllByOrderByIssuedAtDesc();
}
