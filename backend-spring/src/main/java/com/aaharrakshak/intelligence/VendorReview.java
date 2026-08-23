package com.aaharrakshak.intelligence;

import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "vendor_reviews")
public class VendorReview {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "citizen_id")
    private User citizen;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false)
    private Integer rating;

    @Column(length = 1000)
    private String reviewText;

    @Column(nullable = false, length = 500)
    private String receiptObjectKey;

    @Column(nullable = false, length = 180)
    private String receiptFileName;

    @Column(nullable = false, length = 120)
    private String receiptContentType;

    @Column(nullable = false)
    private Long receiptSizeBytes;

    @Column(nullable = false, length = 64)
    private String receiptChecksumSha256;

    @Column(nullable = false)
    private Boolean receiptVerified;

    @Column(nullable = false, length = 120)
    private String receiptVerificationToken;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected VendorReview() {
    }

    public VendorReview(
            User citizen,
            Company company,
            Product product,
            Batch batch,
            Integer rating,
            String reviewText,
            String receiptObjectKey,
            String receiptFileName,
            String receiptContentType,
            Long receiptSizeBytes,
            String receiptChecksumSha256,
            Boolean receiptVerified,
            String receiptVerificationToken) {
        this.citizen = citizen;
        this.company = company;
        this.product = product;
        this.batch = batch;
        this.rating = rating;
        this.reviewText = reviewText;
        this.receiptObjectKey = receiptObjectKey;
        this.receiptFileName = receiptFileName;
        this.receiptContentType = receiptContentType;
        this.receiptSizeBytes = receiptSizeBytes;
        this.receiptChecksumSha256 = receiptChecksumSha256;
        this.receiptVerified = receiptVerified;
        this.receiptVerificationToken = receiptVerificationToken;
    }

    public Long getId() {
        return id;
    }

    public User getCitizen() {
        return citizen;
    }

    public Company getCompany() {
        return company;
    }

    public Integer getRating() {
        return rating;
    }

    public Boolean getReceiptVerified() {
        return receiptVerified;
    }

    public String getReceiptVerificationToken() {
        return receiptVerificationToken;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }
}
