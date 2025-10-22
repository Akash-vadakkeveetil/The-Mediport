package com.mediport.dto;

public class MessageDto {

    private String pharmacyUsername;
    private String medicineCode;
    private String messageText;

    // Constructors
    public MessageDto() {
    }

    public MessageDto(String pharmacyUsername, String medicineCode, String messageText) {
        this.pharmacyUsername = pharmacyUsername;
        this.medicineCode = medicineCode;
        this.messageText = messageText;
    }

    // Getters and Setters
    public String getPharmacyUsername() {
        return pharmacyUsername;
    }

    public void setPharmacyUsername(String pharmacyUsername) {
        this.pharmacyUsername = pharmacyUsername;
    }

    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getMessageText() {
        return messageText;
    }

    public void setMessageText(String messageText) {
        this.messageText = messageText;
    }
}
