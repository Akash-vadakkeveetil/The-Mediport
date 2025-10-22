package com.mediport.repository;

import com.mediport.entity.Message;
import com.mediport.entity.Pharmacy;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

    List<Message> findByPharmacy(Pharmacy pharmacy);

    List<Message> findByPharmacyId(Long pharmacyId);

    List<Message> findByReadStatus(Boolean readStatus);

    List<Message> findByPharmacyAndReadStatus(Pharmacy pharmacy, Boolean readStatus);

    List<Message> findAllByOrderBySentDateDesc();
}
