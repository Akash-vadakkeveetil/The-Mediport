package com.mediport.dto;

import java.time.LocalDate;

public class PharmacyProfileDto {

    private String pharmacyName;
    private String location;
    private Integer pinCode;
    private Long contactNumber;
    private LocalDate establishedDate;

    // Constructors
    public PharmacyProfileDto() {
    }

    public PharmacyProfileDto(String pharmacyName, String location, Integer pinCode, Long contactNumber, LocalDate establishedDate) {
        this.pharmacyName = pharmacyName;
        this.location = location;
        this.pinCode = pinCode;
        this.contactNumber = contactNumber;
        this.establishedDate = establishedDate;
    }

    // Getters and Setters
    public String getPharmacyName() {
        return pharmacyName;
    }

    public void setPharmacyName(String pharmacyName) {
        this.pharmacyName = pharmacyName;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getPinCode() {
        return pinCode;
    }

    public void setPinCode(Integer pinCode) {
        this.pinCode = pinCode;
    }

    public Long getContactNumber() {
        return contactNumber;
    }

    public void setContactNumber(Long contactNumber) {
        this.contactNumber = contactNumber;
    }

    public LocalDate getEstablishedDate() {
        return establishedDate;
    }

    public void setEstablishedDate(LocalDate establishedDate) {
        this.establishedDate = establishedDate;
    }
}
