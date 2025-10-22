package com.mediport.dto;

import java.math.BigDecimal;

public class InventoryDto {

    private String medicineCode;
    private String description;
    private BigDecimal price;
    private Integer minimumQuantity;
    private Integer currentQuantity;

    // Constructors
    public InventoryDto() {
    }

    public InventoryDto(String medicineCode, String description, BigDecimal price, Integer minimumQuantity, Integer currentQuantity) {
        this.medicineCode = medicineCode;
        this.description = description;
        this.price = price;
        this.minimumQuantity = minimumQuantity;
        this.currentQuantity = currentQuantity;
    }

    // Getters and Setters
    public String getMedicineCode() {
        return medicineCode;
    }

    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Integer getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(Integer minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Integer getCurrentQuantity() {
        return currentQuantity;
    }

    public void setCurrentQuantity(Integer currentQuantity) {
        this.currentQuantity = currentQuantity;
    }
}
