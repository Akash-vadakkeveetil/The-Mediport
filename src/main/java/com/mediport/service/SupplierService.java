package com.mediport.service;

import com.mediport.entity.Medicine;
import com.mediport.entity.SupplierCatalog;
import com.mediport.entity.User;
import com.mediport.enums.Availability;
import com.mediport.repository.MedicineRepository;
import com.mediport.repository.SupplierCatalogRepository;
import com.mediport.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class SupplierService {

    @Autowired
    private SupplierCatalogRepository catalogRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public List<SupplierCatalog> getSupplierCatalog(Long supplierId) {
        return catalogRepository.findBySupplierId(supplierId);
    }

    public SupplierCatalog addOrUpdateCatalogItem(Long supplierId, String medicineCode,
                                                   String description, Availability availability) {
        User supplier = userRepository.findById(supplierId)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));

        // Find or create medicine
        Medicine medicine = medicineRepository.findByMedicineCode(medicineCode)
                .orElseGet(() -> {
                    if (description == null || description.isEmpty()) {
                        throw new RuntimeException("Description required for new medicine");
                    }
                    Medicine newMedicine = new Medicine();
                    newMedicine.setMedicineCode(medicineCode);
                    newMedicine.setDescription(description);
                    return medicineRepository.save(newMedicine);
                });

        // Find or create catalog entry
        List<SupplierCatalog> existingCatalog = catalogRepository.findBySupplier(supplier);
        SupplierCatalog catalogEntry = existingCatalog.stream()
                .filter(c -> c.getMedicine().getId().equals(medicine.getId()))
                .findFirst()
                .orElseGet(() -> {
                    SupplierCatalog newEntry = new SupplierCatalog();
                    newEntry.setSupplier(supplier);
                    newEntry.setMedicine(medicine);
                    return newEntry;
                });

        catalogEntry.setAvailability(availability);
        return catalogRepository.save(catalogEntry);
    }

    public List<SupplierCatalog> getAllAvailableSuppliers() {
        return catalogRepository.findAll();
    }
}
