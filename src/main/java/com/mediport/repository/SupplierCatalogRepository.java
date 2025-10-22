package com.mediport.repository;

import com.mediport.entity.Medicine;
import com.mediport.entity.SupplierCatalog;
import com.mediport.entity.User;
import com.mediport.enums.Availability;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupplierCatalogRepository extends JpaRepository<SupplierCatalog, Long> {

    List<SupplierCatalog> findBySupplier(User supplier);

    List<SupplierCatalog> findBySupplierId(Long supplierId);

    List<SupplierCatalog> findByAvailability(Availability availability);

    List<SupplierCatalog> findByMedicine(Medicine medicine);
}
