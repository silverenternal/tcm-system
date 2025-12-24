package com.tcm.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 药品库存实体类
 */
@Entity
@Table(name = "medicine_inventory")
public class MedicineInventory extends BaseEntity {
    
    @Column(name = "medicine_code", unique = true, nullable = false, length = 50)
    private String medicineCode; // 药品编码
    
    @Column(name = "medicine_name", nullable = false, length = 200)
    private String medicineName; // 药品名称
    
    @Column(name = "specification", length = 200)
    private String specification; // 规格
    
    @Column(name = "stock_quantity")
    private Integer stockQuantity; // 库存数量
    
    @Column(name = "supplier", length = 200)
    private String supplier; // 供应商
    
    @Column(name = "production_date")
    private LocalDate productionDate; // 生产日期
    
    @Column(name = "expiry_date")
    private LocalDate expiryDate; // 过期日期
    
    @Column(name = "unit_price", precision = 10, scale = 2)
    private BigDecimal unitPrice; // 单价
    
    @Column(name = "status")
    private Integer status; // 0-停用，1-启用
    
    @Column(name = "description", length = 500)
    private String description; // 药品描述
    
    // 构造函数
    public MedicineInventory() {}
    
    // Getters and Setters
    public String getMedicineCode() {
        return medicineCode;
    }
    
    public void setMedicineCode(String medicineCode) {
        this.medicineCode = medicineCode;
    }
    
    public String getMedicineName() {
        return medicineName;
    }
    
    public void setMedicineName(String medicineName) {
        this.medicineName = medicineName;
    }
    
    public String getSpecification() {
        return specification;
    }
    
    public void setSpecification(String specification) {
        this.specification = specification;
    }
    
    public Integer getStockQuantity() {
        return stockQuantity;
    }
    
    public void setStockQuantity(Integer stockQuantity) {
        this.stockQuantity = stockQuantity;
    }
    
    public String getSupplier() {
        return supplier;
    }
    
    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
    
    public LocalDate getProductionDate() {
        return productionDate;
    }
    
    public void setProductionDate(LocalDate productionDate) {
        this.productionDate = productionDate;
    }
    
    public LocalDate getExpiryDate() {
        return expiryDate;
    }
    
    public void setExpiryDate(LocalDate expiryDate) {
        this.expiryDate = expiryDate;
    }
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public void setUnitPrice(BigDecimal unitPrice) {
        this.unitPrice = unitPrice;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
}