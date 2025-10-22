package com.mediport.repository;

import com.mediport.entity.Medicine;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.PharmacyInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface PharmacyInventoryRepository extends JpaRepository<PharmacyInventory, Long> {

    List<PharmacyInventory> findByPharmacy(Pharmacy pharmacy);

    List<PharmacyInventory> findByPharmacyId(Long pharmacyId);

    Optional<PharmacyInventory> findByPharmacyAndMedicine(Pharmacy pharmacy, Medicine medicine);

    @Query("SELECT pi FROM PharmacyInventory pi WHERE pi.currentQuantity < pi.minimumQuantity")
    List<PharmacyInventory> findLowStockItems();

    @Query("SELECT pi FROM PharmacyInventory pi WHERE pi.pharmacy.id = :pharmacyId AND pi.currentQuantity < pi.minimumQuantity")
    List<PharmacyInventory> findLowStockItemsByPharmacy(@Param("pharmacyId") Long pharmacyId);
}
