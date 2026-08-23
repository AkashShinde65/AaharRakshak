package com.aaharrakshak.catalog;

import com.aaharrakshak.company.Company;
import com.aaharrakshak.storage.StoredFileMetadata;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import java.time.Instant;

@Entity
@Table(name = "products")
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(nullable = false, length = 180)
    private String name;

    @Column(length = 80)
    private String barcode;

    @Column(length = 80)
    private String category;

    @Column(length = 120)
    private String brand;

    @Column(length = 180)
    private String manufacturerName;

    @Column(length = 1000)
    private String description;

    @Column(length = 500)
    private String frontLabelObjectKey;

    @Column(length = 180)
    private String frontLabelFileName;

    @Column(length = 120)
    private String frontLabelContentType;

    private Long frontLabelSizeBytes;

    @Column(length = 500)
    private String licenceLabelObjectKey;

    @Column(length = 180)
    private String licenceLabelFileName;

    @Column(length = 120)
    private String licenceLabelContentType;

    private Long licenceLabelSizeBytes;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(nullable = false)
    private Instant updatedAt = Instant.now();

    protected Product() {
    }

    public Product(
            Company company,
            String name,
            String barcode,
            String category,
            String brand,
            String manufacturerName,
            String description,
            StoredFileMetadata frontLabel,
            StoredFileMetadata licenceLabel) {
        this.company = company;
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.brand = brand;
        this.manufacturerName = manufacturerName;
        this.description = description;
        applyFrontLabel(frontLabel);
        applyLicenceLabel(licenceLabel);
    }

    @PreUpdate
    void markUpdated() {
        this.updatedAt = Instant.now();
    }

    public Long getId() {
        return id;
    }

    public Company getCompany() {
        return company;
    }

    public String getName() {
        return name;
    }

    public String getBarcode() {
        return barcode;
    }

    public String getCategory() {
        return category;
    }

    public String getBrand() {
        return brand;
    }

    public String getManufacturerName() {
        return manufacturerName;
    }

    public String getDescription() {
        return description;
    }

    public String getFrontLabelObjectKey() {
        return frontLabelObjectKey;
    }

    public String getFrontLabelFileName() {
        return frontLabelFileName;
    }

    public String getFrontLabelContentType() {
        return frontLabelContentType;
    }

    public Long getFrontLabelSizeBytes() {
        return frontLabelSizeBytes;
    }

    public String getLicenceLabelObjectKey() {
        return licenceLabelObjectKey;
    }

    public String getLicenceLabelFileName() {
        return licenceLabelFileName;
    }

    public String getLicenceLabelContentType() {
        return licenceLabelContentType;
    }

    public Long getLicenceLabelSizeBytes() {
        return licenceLabelSizeBytes;
    }

    public void update(
            String name,
            String barcode,
            String category,
            String brand,
            String manufacturerName,
            String description,
            StoredFileMetadata frontLabel,
            StoredFileMetadata licenceLabel) {
        this.name = name;
        this.barcode = barcode;
        this.category = category;
        this.brand = brand;
        this.manufacturerName = manufacturerName;
        this.description = description;
        applyFrontLabel(frontLabel);
        applyLicenceLabel(licenceLabel);
    }

    private void applyFrontLabel(StoredFileMetadata frontLabel) {
        if (frontLabel == null) {
            return;
        }
        this.frontLabelObjectKey = frontLabel.objectKey();
        this.frontLabelFileName = frontLabel.originalFileName();
        this.frontLabelContentType = frontLabel.contentType();
        this.frontLabelSizeBytes = frontLabel.sizeBytes();
    }

    private void applyLicenceLabel(StoredFileMetadata licenceLabel) {
        if (licenceLabel == null) {
            return;
        }
        this.licenceLabelObjectKey = licenceLabel.objectKey();
        this.licenceLabelFileName = licenceLabel.originalFileName();
        this.licenceLabelContentType = licenceLabel.contentType();
        this.licenceLabelSizeBytes = licenceLabel.sizeBytes();
    }
}
