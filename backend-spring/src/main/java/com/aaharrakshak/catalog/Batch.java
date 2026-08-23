package com.aaharrakshak.catalog;

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
import jakarta.persistence.UniqueConstraint;
import java.time.LocalDate;

@Entity
@Table(name = "batches", uniqueConstraints = @UniqueConstraint(columnNames = {"product_id", "batch_number"}))
public class Batch {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, length = 80)
    private String batchNumber;

    private LocalDate manufacturedOn;

    private LocalDate expiresOn;

    @Column(nullable = false, length = 40)
    @Enumerated(EnumType.STRING)
    private BatchStatus status = BatchStatus.ACTIVE;

    protected Batch() {
    }

    public Batch(Product product, String batchNumber, LocalDate manufacturedOn, LocalDate expiresOn, BatchStatus status) {
        this.product = product;
        this.batchNumber = batchNumber;
        this.manufacturedOn = manufacturedOn;
        this.expiresOn = expiresOn;
        this.status = status == null ? BatchStatus.ACTIVE : status;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getBatchNumber() {
        return batchNumber;
    }

    public LocalDate getManufacturedOn() {
        return manufacturedOn;
    }

    public LocalDate getExpiresOn() {
        return expiresOn;
    }

    public BatchStatus getStatus() {
        return status;
    }

    public void update(String batchNumber, LocalDate manufacturedOn, LocalDate expiresOn, BatchStatus status) {
        this.batchNumber = batchNumber;
        this.manufacturedOn = manufacturedOn;
        this.expiresOn = expiresOn;
        this.status = status == null ? BatchStatus.ACTIVE : status;
    }
}
