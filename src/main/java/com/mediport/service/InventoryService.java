package com.mediport.service;

import com.mediport.dto.InventoryDto;
import com.mediport.entity.Medicine;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.PharmacyInventory;
import com.mediport.repository.MedicineRepository;
import com.mediport.repository.PharmacyInventoryRepository;
import com.mediport.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class InventoryService {

    @Autowired
    private PharmacyInventoryRepository inventoryRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public List<PharmacyInventory> getPharmacyStock(Long pharmacyId) {
        return inventoryRepository.findByPharmacyId(pharmacyId);
    }

    public PharmacyInventory addOrUpdateStock(InventoryDto dto, Long pharmacyId) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));

        // Find or create medicine
        Medicine medicine = medicineRepository.findByMedicineCode(dto.getMedicineCode())
                .orElseGet(() -> {
                    if (dto.getDescription() == null || dto.getDescription().isEmpty()) {
                        throw new RuntimeException("Description required for new medicine");
                    }
                    Medicine newMedicine = new Medicine();
                    newMedicine.setMedicineCode(dto.getMedicineCode());
                    newMedicine.setDescription(dto.getDescription());
                    return medicineRepository.save(newMedicine);
                });

        // Find or create inventory entry
        Optional<PharmacyInventory> existingInventory =
                inventoryRepository.findByPharmacyAndMedicine(pharmacy, medicine);

        PharmacyInventory inventory;
        if (existingInventory.isPresent()) {
            // Update existing
            inventory = existingInventory.get();
            if (dto.getPrice() != null) {
                inventory.setPrice(dto.getPrice());
            }
            inventory.setCurrentQuantity(dto.getCurrentQuantity());
        } else {
            // Create new
            inventory = new PharmacyInventory();
            inventory.setPharmacy(pharmacy);
            inventory.setMedicine(medicine);
            inventory.setPrice(dto.getPrice());
            inventory.setMinimumQuantity(dto.getMinimumQuantity() != null ? dto.getMinimumQuantity() : 0);
            inventory.setCurrentQuantity(dto.getCurrentQuantity());
        }

        inventory.setLastUpdated(LocalDate.now());
        return inventoryRepository.save(inventory);
    }

    public List<PharmacyInventory> getAllLowStockItems() {
        return inventoryRepository.findLowStockItems();
    }

    public List<PharmacyInventory> getLowStockItemsByPharmacy(Long pharmacyId) {
        return inventoryRepository.findLowStockItemsByPharmacy(pharmacyId);
    }

    public void updateMinimumQuantity(Long pharmacyId, String medicineCode, Integer newMinimum) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));

        Medicine medicine = medicineRepository.findByMedicineCode(medicineCode)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        PharmacyInventory inventory = inventoryRepository.findByPharmacyAndMedicine(pharmacy, medicine)
                .orElseThrow(() -> new RuntimeException("Inventory entry not found"));

        inventory.setMinimumQuantity(newMinimum);
        inventoryRepository.save(inventory);
    }
}
