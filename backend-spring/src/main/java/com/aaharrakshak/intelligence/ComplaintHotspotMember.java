package com.aaharrakshak.intelligence;

import com.aaharrakshak.complaint.Complaint;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "complaint_hotspot_members")
public class ComplaintHotspotMember {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "hotspot_id")
    private ComplaintHotspot hotspot;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "complaint_id")
    private Complaint complaint;

    protected ComplaintHotspotMember() {
    }

    public ComplaintHotspotMember(ComplaintHotspot hotspot, Complaint complaint) {
        this.hotspot = hotspot;
        this.complaint = complaint;
    }

    public ComplaintHotspot getHotspot() {
        return hotspot;
    }

    public Complaint getComplaint() {
        return complaint;
    }
}
