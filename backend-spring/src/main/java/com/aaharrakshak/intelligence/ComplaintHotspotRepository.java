package com.aaharrakshak.intelligence;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintHotspotRepository extends JpaRepository<ComplaintHotspot, Long> {

    boolean existsByHotspotKey(String hotspotKey);

    Optional<ComplaintHotspot> findByHotspotKey(String hotspotKey);

    List<ComplaintHotspot> findByActiveTrueOrderByDetectedAtDesc();

    List<ComplaintHotspot> findByDistrictIgnoreCaseAndActiveTrueOrderByDetectedAtDesc(String district);
}
