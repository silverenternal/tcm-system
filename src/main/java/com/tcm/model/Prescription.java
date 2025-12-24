package com.tcm.model;

import jakarta.persistence.*;

import java.util.List;

/**
 * 处方实体类
 */
@Entity
@Table(name = "prescriptions")
public class Prescription extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "doctor_id")
    private Doctor doctor;
    
    @Column(name = "prescription_name", length = 200)
    private String prescriptionName;
    
    @Column(name = "decoction_method", length = 500)
    private String decoctionMethod; // 煎药方法
    
    @Column(name = "treatment_duration")
    private Integer treatmentDuration; // 治疗天数
    
    @Column(name = "doctor_advice", length = 1000)
    private String doctorAdvice; // 医嘱
    
    @Column(name = "status")
    private Integer status; // 0-未取药，1-已取药，2-已完成
    
    // 关联处方明细
    @OneToMany(mappedBy = "prescription", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<PrescriptionDetail> prescriptionDetails;
    
    // 构造函数
    public Prescription() {}
    
    // Getters and Setters
    public Visit getVisit() {
        return visit;
    }
    
    public void setVisit(Visit visit) {
        this.visit = visit;
    }
    
    public Doctor getDoctor() {
        return doctor;
    }
    
    public void setDoctor(Doctor doctor) {
        this.doctor = doctor;
    }
    
    public String getPrescriptionName() {
        return prescriptionName;
    }
    
    public void setPrescriptionName(String prescriptionName) {
        this.prescriptionName = prescriptionName;
    }
    
    public String getDecoctionMethod() {
        return decoctionMethod;
    }
    
    public void setDecoctionMethod(String decoctionMethod) {
        this.decoctionMethod = decoctionMethod;
    }
    
    public Integer getTreatmentDuration() {
        return treatmentDuration;
    }
    
    public void setTreatmentDuration(Integer treatmentDuration) {
        this.treatmentDuration = treatmentDuration;
    }
    
    public String getDoctorAdvice() {
        return doctorAdvice;
    }
    
    public void setDoctorAdvice(String doctorAdvice) {
        this.doctorAdvice = doctorAdvice;
    }
    
    public Integer getStatus() {
        return status;
    }
    
    public void setStatus(Integer status) {
        this.status = status;
    }
    
    public List<PrescriptionDetail> getPrescriptionDetails() {
        return prescriptionDetails;
    }
    
    public void setPrescriptionDetails(List<PrescriptionDetail> prescriptionDetails) {
        this.prescriptionDetails = prescriptionDetails;
    }
}