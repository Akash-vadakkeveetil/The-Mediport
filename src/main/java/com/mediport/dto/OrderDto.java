package com.mediport.dto;

public class OrderDto {

    private String medicineCode;
    private Integer quantity;
    private String supplierUsername;

    // Constructors
    public OrderDto() {
    }

    public OrderDto(String medicineCode, Integer quantity, String supplierUsername) {
        this.medicineCode = medicineCode;
        this.quantity = quantity;
        this.supplierUsername = supplierUsername;
    }

    // Getters and Setters
    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public String getSupplierUsername() {
        return supplierUsername;
    }

    public void setSupplierUsername(String supplierUsername) {
        this.supplierUsername = supplierUsername;
    }
}
