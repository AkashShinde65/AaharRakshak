package com.aaharrakshak.auth;

import com.aaharrakshak.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "otp_verifications")
public class OtpVerification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id")
    private User user;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private OtpChannel channel;

    @Column(nullable = false, length = 160)
    private String destination;

    @Column(nullable = false, length = 12)
    private String code;

    @Column(nullable = false)
    private Boolean verified = false;

    @Column(nullable = false)
    private Instant expiresAt;

    private Instant verifiedAt;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected OtpVerification() {
    }

    public OtpVerification(User user, OtpChannel channel, String destination, String code, Instant expiresAt) {
        this.user = user;
        this.channel = channel;
        this.destination = destination;
        this.code = code;
        this.expiresAt = expiresAt;
    }

    public User getUser() {
        return user;
    }

    public OtpChannel getChannel() {
        return channel;
    }

    public String getCode() {
        return code;
    }

    public Instant getExpiresAt() {
        return expiresAt;
    }

    public Boolean getVerified() {
        return verified;
    }

    public void markVerified() {
        this.verified = true;
        this.verifiedAt = Instant.now();
    }
}

