package com.tcm.model;

import jakarta.persistence.*;

import java.math.BigDecimal;

/**
 * 处方明细实体类
 */
@Entity
@Table(name = "prescription_details")
public class PrescriptionDetail extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "prescription_id")
    private Prescription prescription;
    
    @Column(name = "herb_name", nullable = false, length = 200)
    private String herbName; // 药材名称
    
    @Column(name = "dosage", precision = 10, scale = 2)
    private BigDecimal dosage; // 用量
    
    @Column(name = "unit", length = 20)
    private String unit; // 单位，如"g", "ml", "片"
    
    @Column(name = "properties", length = 500)
    private String properties; // 药材性质
    
    @Column(name = "usage_instructions", length = 500)
    private String usageInstructions; // 用法说明
    
    // 构造函数
    public PrescriptionDetail() {}
    
    // Getters and Setters
    public Prescription getPrescription() {
        return prescription;
    }
    
    public void setPrescription(Prescription prescription) {
        this.prescription = prescription;
    }
    
    public String getHerbName() {
        return herbName;
    }
    
    public void setHerbName(String herbName) {
        this.herbName = herbName;
    }
    
    public BigDecimal getDosage() {
        return dosage;
    }
    
    public void setDosage(BigDecimal dosage) {
        this.dosage = dosage;
    }
    
    public String getUnit() {
        return unit;
    }
    
    public void setUnit(String unit) {
        this.unit = unit;
    }
    
    public String getProperties() {
        return properties;
    }
    
    public void setProperties(String properties) {
        this.properties = properties;
    }
    
    public String getUsageInstructions() {
        return usageInstructions;
    }
    
    public void setUsageInstructions(String usageInstructions) {
        this.usageInstructions = usageInstructions;
    }
}