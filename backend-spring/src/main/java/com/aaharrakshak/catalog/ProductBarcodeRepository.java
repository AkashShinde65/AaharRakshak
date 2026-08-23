package com.aaharrakshak.catalog;

import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductBarcodeRepository extends JpaRepository<ProductBarcode, Long> {

    boolean existsByBarcode(String barcode);

    Optional<ProductBarcode> findByBarcode(String barcode);

    List<ProductBarcode> findByProductIdOrderByPrimaryCodeDescBarcodeAsc(Long productId);
}
