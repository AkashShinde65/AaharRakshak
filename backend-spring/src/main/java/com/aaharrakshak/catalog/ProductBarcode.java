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
import java.time.Instant;

@Entity
@Table(name = "product_barcodes")
public class ProductBarcode {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "product_id")
    private Product product;

    @Column(nullable = false, unique = true, length = 32)
    private String barcode;

    @Column(nullable = false, length = 20)
    @Enumerated(EnumType.STRING)
    private ProductBarcodeType barcodeType;

    @Column(nullable = false)
    private Boolean primaryCode = false;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    protected ProductBarcode() {
    }

    public ProductBarcode(Product product, String barcode, ProductBarcodeType barcodeType, boolean primaryCode) {
        this.product = product;
        this.barcode = barcode;
        this.barcodeType = barcodeType;
        this.primaryCode = primaryCode;
    }

    public Long getId() {
        return id;
    }

    public Product getProduct() {
        return product;
    }

    public String getBarcode() {
        return barcode;
    }

    public ProductBarcodeType getBarcodeType() {
        return barcodeType;
    }

    public Boolean getPrimaryCode() {
        return primaryCode;
    }
}
