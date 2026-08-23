package com.aaharrakshak.action;

import com.aaharrakshak.catalog.Batch;
import com.aaharrakshak.catalog.Product;
import com.aaharrakshak.company.Company;
import com.aaharrakshak.complaint.Complaint;
import com.aaharrakshak.investigation.Action;
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
@Table(name = "safety_alerts")
public class SafetyAlert {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "action_id")
    private Action action;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id")
    private Product product;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @Column(nullable = false, length = 180)
    private String title;

    @Column(nullable = false, length = 1200)
    private String message;

    @Column(length = 120)
    private String locationText;

    @Column(nullable = false, length = 40)
    private String severity;

    @Column(nullable = false)
    private Boolean active = true;

    @Column(nullable = false, updatable = false)
    private Instant publishedAt = Instant.now();

    protected SafetyAlert() {
    }

    public SafetyAlert(Action action, String title, String message, String severity) {
        this.action = action;
        this.complaint = action.getComplaint();
        this.company = action.getCompany();
        this.product = action.getComplaint().getProduct();
        this.batch = action.getComplaint().getBatch();
        this.title = title;
        this.message = message;
        this.locationText = action.getComplaint().getDistrict();
        this.severity = severity;
        this.active = true;
    }

    public Long getId() {
        return id;
    }

    public Action getAction() {
        return action;
    }

    public Complaint getComplaint() {
        return complaint;
    }

    public Company getCompany() {
        return company;
    }

    public Product getProduct() {
        return product;
    }

    public Batch getBatch() {
        return batch;
    }

    public String getTitle() {
        return title;
    }

    public String getMessage() {
        return message;
    }

    public String getLocationText() {
        return locationText;
    }

    public String getSeverity() {
        return severity;
    }

    public Boolean getActive() {
        return active;
    }

    public Instant getPublishedAt() {
        return publishedAt;
    }
}
