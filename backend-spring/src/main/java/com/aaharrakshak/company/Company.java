package com.aaharrakshak.company;

import com.aaharrakshak.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "companies")
public class Company {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 180)
    private String legalName;

    @Column(length = 180)
    private String tradeName;

    @Column(length = 30)
    private String gstin;

    @Column(length = 300)
    private String registeredAddress;

    @Column(length = 160)
    private String contactEmail;

    @Column(length = 20)
    private String contactMobile;

    @Column(length = 180)
    private String websiteUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "owner_user_id")
    private User ownerUser;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private CompanyStatus status = CompanyStatus.PENDING_VERIFICATION;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Company() {
    }

    public Company(String legalName, String tradeName, String gstin, User ownerUser) {
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.gstin = gstin;
        this.ownerUser = ownerUser;
        this.status = CompanyStatus.PENDING_VERIFICATION;
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public String getLegalName() {
        return legalName;
    }

    public String getTradeName() {
        return tradeName;
    }

    public String getGstin() {
        return gstin;
    }

    public String getRegisteredAddress() {
        return registeredAddress;
    }

    public String getContactEmail() {
        return contactEmail;
    }

    public String getContactMobile() {
        return contactMobile;
    }

    public String getWebsiteUrl() {
        return websiteUrl;
    }

    public User getOwnerUser() {
        return ownerUser;
    }

    public CompanyStatus getStatus() {
        return status;
    }

    public void verify() {
        this.status = CompanyStatus.VERIFIED;
    }

    public void updateProfile(
            String legalName,
            String tradeName,
            String gstin,
            String registeredAddress,
            String contactEmail,
            String contactMobile,
            String websiteUrl) {
        this.legalName = legalName;
        this.tradeName = tradeName;
        this.gstin = gstin;
        this.registeredAddress = registeredAddress;
        this.contactEmail = contactEmail;
        this.contactMobile = contactMobile;
        this.websiteUrl = websiteUrl;
    }
}
