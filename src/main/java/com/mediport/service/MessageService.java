package com.mediport.service;

import com.mediport.entity.Medicine;
import com.mediport.entity.Message;
import com.mediport.entity.Pharmacy;
import com.mediport.repository.MedicineRepository;
import com.mediport.repository.MessageRepository;
import com.mediport.repository.PharmacyRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@Transactional
public class MessageService {

    @Autowired
    private MessageRepository messageRepository;

    @Autowired
    private PharmacyRepository pharmacyRepository;

    @Autowired
    private MedicineRepository medicineRepository;

    public Message sendMessage(Long pharmacyId, String medicineCode, String messageText) {
        Pharmacy pharmacy = pharmacyRepository.findById(pharmacyId)
                .orElseThrow(() -> new RuntimeException("Pharmacy not found"));

        Medicine medicine = medicineRepository.findByMedicineCode(medicineCode)
                .orElseThrow(() -> new RuntimeException("Medicine not found"));

        Message message = new Message();
        message.setPharmacy(pharmacy);
        message.setMedicine(medicine);
        message.setMessageText(messageText);
        message.setSentDate(LocalDate.now());
        message.setReadStatus(false);

        return messageRepository.save(message);
    }

    public List<Message> getPharmacyMessages(Long pharmacyId) {
        return messageRepository.findByPharmacyId(pharmacyId);
    }

    public List<Message> getAllMessages() {
        return messageRepository.findAllByOrderBySentDateDesc();
    }

    public void markAsRead(Long messageId) {
        Message message = messageRepository.findById(messageId)
                .orElseThrow(() -> new RuntimeException("Message not found"));
        message.setReadStatus(true);
        messageRepository.save(message);
    }
}
