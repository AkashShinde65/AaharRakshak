package com.aaharrakshak.intelligence;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ComplaintHotspotMemberRepository extends JpaRepository<ComplaintHotspotMember, Long> {

    List<ComplaintHotspotMember> findByHotspotId(Long hotspotId);
}
