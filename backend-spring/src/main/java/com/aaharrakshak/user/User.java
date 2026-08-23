package com.aaharrakshak.user;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 120)
    private String fullName;

    @Column(length = 160, unique = true)
    private String email;

    @Column(length = 20, unique = true)
    private String mobileNumber;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private UserStatus status = UserStatus.PENDING_VERIFICATION;

    @Column(name = "password_hash", length = 255)
    private String passwordHash;

    @Column(nullable = false)
    private Integer failedLoginAttempts = 0;

    private Instant lockedUntil;

    private Instant lastLoginAt;

    @Column(nullable = false)
    private Boolean emailVerified = false;

    @Column(nullable = false)
    private Boolean mobileVerified = false;

    @Column(length = 120)
    private String identityVerificationToken;

    @Column(length = 30)
    private String identityVerificationStatus;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected User() {
    }

    public User(String fullName, String email, String mobileNumber, String passwordHash, UserStatus status) {
        this.fullName = fullName;
        this.email = email;
        this.mobileNumber = mobileNumber;
        this.passwordHash = passwordHash;
        this.status = status;
        this.identityVerificationStatus = "UNVERIFIED";
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getEmail() {
        return email;
    }

    public String getMobileNumber() {
        return mobileNumber;
    }

    public UserStatus getStatus() {
        return status;
    }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Integer getFailedLoginAttempts() {
        return failedLoginAttempts;
    }

    public void recordFailedLogin(int maxAttempts, Instant lockedUntil) {
        this.failedLoginAttempts = this.failedLoginAttempts + 1;
        if (this.failedLoginAttempts >= maxAttempts) {
            this.lockedUntil = lockedUntil;
        }
    }

    public void recordSuccessfulLogin() {
        this.failedLoginAttempts = 0;
        this.lockedUntil = null;
        this.lastLoginAt = Instant.now();
    }

    public Instant getLockedUntil() {
        return lockedUntil;
    }

    public boolean isLocked(Instant now) {
        return lockedUntil != null && lockedUntil.isAfter(now);
    }

    public Boolean getEmailVerified() {
        return emailVerified;
    }

    public Boolean getMobileVerified() {
        return mobileVerified;
    }

    public void markEmailVerified() {
        this.emailVerified = true;
        activateIfPending();
    }

    public void markMobileVerified() {
        this.mobileVerified = true;
        activateIfPending();
    }

    public String getIdentityVerificationToken() {
        return identityVerificationToken;
    }

    public String getIdentityVerificationStatus() {
        return identityVerificationStatus;
    }

    public void markMockAadhaarVerified(String verificationToken) {
        this.identityVerificationToken = verificationToken;
        this.identityVerificationStatus = "MOCK_AADHAAR_VERIFIED";
    }

    private void activateIfPending() {
        if (this.status == UserStatus.PENDING_VERIFICATION) {
            this.status = UserStatus.ACTIVE;
        }
    }
}
