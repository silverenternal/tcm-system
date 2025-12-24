package com.tcm.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 就诊记录实体类
 */
@Entity
@Table(name = "visits")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "diagnosticImages"})
public class Visit extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "patient_id")
    @com.fasterxml.jackson.annotation.JsonBackReference
    private Patient patient;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @Column(name = "visit_type")
    private Integer visitType; // 0-初诊，1-复诊
    
    @Column(name = "medical_record_number", unique = true, length = 50)
    private String medicalRecordNumber;
    
    @Column(name = "chief_complaint", length = 500)
    private String chiefComplaint; // 主诉
    
    @Column(name = "symptoms", length = 1000)
    private String symptoms; // 症状
    
    @Column(name = "initial_visit_clinical_manifestation", length = 1000)
    private String initialVisitClinicalManifestation; // 初诊临床表现
    
    @Column(name = "follow_up_clinical_manifestation", length = 1000)
    private String followUpClinicalManifestation; // 复诊临床表现
    
    @Column(name = "tongue_diagnosis", length = 500)
    private String tongueDiagnosis; // 舌诊
    
    @Column(name = "pulse_diagnosis", length = 500)
    private String pulseDiagnosis; // 脉诊
    
    @Column(name = "tcm_diagnosis", length = 500)
    private String tcmDiagnosis; // 中医诊断
    
    @Column(name = "western_diagnosis", length = 500)
    private String westernDiagnosis; // 西医诊断
    
    @Column(name = "pattern_differentiation", length = 500)
    private String patternDifferentiation; // 证型
    
    @Column(name = "treatment_plan", length = 1000)
    private String treatmentPlan; // 治疗方案
    
    @Column(name = "visit_date")
    private LocalDateTime visitDate;
    
    @Column(name = "tongue_image_path", length = 500)
    private String tongueImagePath; // 舌象图片路径

    @Column(name = "ai_analysis_raw_response", columnDefinition = "TEXT")
    private String aiAnalysisRawResponse; // AI分析的原始响应，用于调试和验证

    // 与诊断图片的关联关系
    @OneToMany(mappedBy = "visit", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private java.util.List<DiagnosticImage> diagnosticImages;
    
    // 构造函数
    public Visit() {}
    
    // Getters and Setters
    public Patient getPatient() {
        return patient;
    }
    
    public void setPatient(Patient patient) {
        this.patient = patient;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public Integer getVisitType() {
        return visitType;
    }
    
    public void setVisitType(Integer visitType) {
        this.visitType = visitType;
    }
    
    public String getMedicalRecordNumber() {
        return medicalRecordNumber;
    }
    
    public void setMedicalRecordNumber(String medicalRecordNumber) {
        this.medicalRecordNumber = medicalRecordNumber;
    }
    
    public String getChiefComplaint() {
        return chiefComplaint;
    }
    
    public void setChiefComplaint(String chiefComplaint) {
        this.chiefComplaint = chiefComplaint;
    }
    
    public String getSymptoms() {
        return symptoms;
    }
    
    public void setSymptoms(String symptoms) {
        this.symptoms = symptoms;
    }
    
    public String getInitialVisitClinicalManifestation() {
        return initialVisitClinicalManifestation;
    }
    
    public void setInitialVisitClinicalManifestation(String initialVisitClinicalManifestation) {
        this.initialVisitClinicalManifestation = initialVisitClinicalManifestation;
    }
    
    public String getFollowUpClinicalManifestation() {
        return followUpClinicalManifestation;
    }
    
    public void setFollowUpClinicalManifestation(String followUpClinicalManifestation) {
        this.followUpClinicalManifestation = followUpClinicalManifestation;
    }
    
    public String getTongueDiagnosis() {
        return tongueDiagnosis;
    }
    
    public void setTongueDiagnosis(String tongueDiagnosis) {
        this.tongueDiagnosis = tongueDiagnosis;
    }
    
    public String getPulseDiagnosis() {
        return pulseDiagnosis;
    }
    
    public void setPulseDiagnosis(String pulseDiagnosis) {
        this.pulseDiagnosis = pulseDiagnosis;
    }
    
    public String getTcmDiagnosis() {
        return tcmDiagnosis;
    }
    
    public void setTcmDiagnosis(String tcmDiagnosis) {
        this.tcmDiagnosis = tcmDiagnosis;
    }
    
    public String getWesternDiagnosis() {
        return westernDiagnosis;
    }
    
    public void setWesternDiagnosis(String westernDiagnosis) {
        this.westernDiagnosis = westernDiagnosis;
    }
    
    public String getPatternDifferentiation() {
        return patternDifferentiation;
    }
    
    public void setPatternDifferentiation(String patternDifferentiation) {
        this.patternDifferentiation = patternDifferentiation;
    }
    
    public String getTreatmentPlan() {
        return treatmentPlan;
    }
    
    public void setTreatmentPlan(String treatmentPlan) {
        this.treatmentPlan = treatmentPlan;
    }
    
    public LocalDateTime getVisitDate() {
        return visitDate;
    }
    
    public void setVisitDate(LocalDateTime visitDate) {
        this.visitDate = visitDate;
    }
    
    public String getTongueImagePath() {
        return tongueImagePath;
    }

    public void setTongueImagePath(String tongueImagePath) {
        this.tongueImagePath = tongueImagePath;
    }

    public String getAiAnalysisRawResponse() {
        return aiAnalysisRawResponse;
    }

    public void setAiAnalysisRawResponse(String aiAnalysisRawResponse) {
        this.aiAnalysisRawResponse = aiAnalysisRawResponse;
    }

    public java.util.List<DiagnosticImage> getDiagnosticImages() {
        return diagnosticImages;
    }

    public void setDiagnosticImages(java.util.List<DiagnosticImage> diagnosticImages) {
        this.diagnosticImages = diagnosticImages;
    }
}