package com.tcm.model;

import jakarta.persistence.*;

/**
 * 病史记录实体类
 */
@Entity
@Table(name = "medical_histories")
public class MedicalHistory extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    private Patient patient;
    
    @Column(name = "medical_history", length = 1000)
    private String medicalHistory; // 既往病史
    
    @Column(name = "allergy_history", length = 500)
    private String allergyHistory; // 过敏史
    
    @Column(name = "family_medical_history", length = 1000)
    private String familyMedicalHistory; // 家族病史
    
    @Column(name = "lifestyle_info", length = 500)
    private String lifestyleInfo; // 生活方式信息
    
    @Column(name = "emotional_state", length = 500)
    private String emotionalState; // 情绪状态
    
    @Column(name = "menstrual_history", length = 500)
    private String menstrualHistory; // 月经史（针对女性患者）
    
    @Column(name = "pregnancy_history", length = 500)
    private String pregnancyHistory; // 生育史（针对女性患者）
    
    // 构造函数
    public MedicalHistory() {}
    
    // Getters and Setters
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public String getMedicalHistory() {
        return medicalHistory;
    }
    
    public void setMedicalHistory(String medicalHistory) {
        this.medicalHistory = medicalHistory;
    }
    
    public String getAllergyHistory() {
        return allergyHistory;
    }
    
    public void setAllergyHistory(String allergyHistory) {
        this.allergyHistory = allergyHistory;
    }
    
    public String getFamilyMedicalHistory() {
        return familyMedicalHistory;
    }
    
    public void setFamilyMedicalHistory(String familyMedicalHistory) {
        this.familyMedicalHistory = familyMedicalHistory;
    }
    
    public String getLifestyleInfo() {
        return lifestyleInfo;
    }
    
    public void setLifestyleInfo(String lifestyleInfo) {
        this.lifestyleInfo = lifestyleInfo;
    }
    
    public String getEmotionalState() {
        return emotionalState;
    }
    
    public void setEmotionalState(String emotionalState) {
        this.emotionalState = emotionalState;
    }
    
    public String getMenstrualHistory() {
        return menstrualHistory;
    }
    
    public void setMenstrualHistory(String menstrualHistory) {
        this.menstrualHistory = menstrualHistory;
    }
    
    public String getPregnancyHistory() {
        return pregnancyHistory;
    }
    
    public void setPregnancyHistory(String pregnancyHistory) {
        this.pregnancyHistory = pregnancyHistory;
    }
}