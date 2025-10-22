package com.mediport.repository;

import com.mediport.entity.Pharmacy;
import com.mediport.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PharmacyRepository extends JpaRepository<Pharmacy, Long> {

    Optional<Pharmacy> findByUser(User user);

    Optional<Pharmacy> findByUserId(Long userId);
}
