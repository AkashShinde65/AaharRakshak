package com.aaharrakshak.auth;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OtpVerificationRepository extends JpaRepository<OtpVerification, Long> {

    Optional<OtpVerification> findFirstByDestinationAndChannelAndVerifiedFalseOrderByCreatedAtDesc(
            String destination, OtpChannel channel);
}

