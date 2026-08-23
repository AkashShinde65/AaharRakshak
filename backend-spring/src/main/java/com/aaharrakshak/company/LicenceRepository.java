package com.aaharrakshak.company;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LicenceRepository extends JpaRepository<Licence, Long> {

    boolean existsByLicenceNumber(String licenceNumber);

    Optional<Licence> findByLicenceNumber(String licenceNumber);

    List<Licence> findByCompanyOwnerUserIdOrderByIdDesc(Long ownerUserId);
}
