package com.mediport.service;

import com.mediport.dto.PharmacyProfileDto;
import com.mediport.entity.Pharmacy;
import com.mediport.entity.User;
import com.mediport.enums.UserRole;
import com.mediport.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Transactional
public class PharmacyService {

    @Autowired
    private PharmacyRepository pharmacyRepository;

    public Pharmacy createPharmacyProfile(PharmacyProfileDto dto, User user) {
        // Validate user has PHARMACY role
        if (user.getRole() != UserRole.PHARMACY) {
            throw new RuntimeException("User must have PHARMACY role");
        }

        // Create pharmacy entity
        Pharmacy pharmacy = new Pharmacy();
        pharmacy.setUser(user);
        pharmacy.setPharmacyName(dto.getPharmacyName());
        pharmacy.setLocation(dto.getLocation());
        pharmacy.setPinCode(dto.getPinCode());
        pharmacy.setContactNumber(dto.getContactNumber());
        pharmacy.setEstablishedDate(dto.getEstablishedDate());

        return pharmacyRepository.save(pharmacy);
    }

    public Pharmacy findByUser(User user) {
        return pharmacyRepository.findByUser(user)
                .orElseThrow(() -> new RuntimeException("Pharmacy profile not found for user"));
    }

    public Pharmacy findByUserId(Long userId) {
        return pharmacyRepository.findByUserId(userId)
                .orElseThrow(() -> new RuntimeException("Pharmacy profile not found"));
    }

    public List<Pharmacy> findAllPharmacies() {
        return pharmacyRepository.findAll();
    }
}
