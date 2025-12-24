package com.tcm.model;

import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * 随访记录实体类
 */
@Entity
@Table(name = "follow_ups")
public class FollowUp extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    private Visit visit;
    
    @Column(name = "follow_up_date")
    private LocalDate followUpDate; // 随访日期
    
    @Column(name = "follow_up_content", length = 1000)
    private String followUpContent; // 随访内容
    
    @Column(name = "patient_condition", length = 500)
    private String patientCondition; // 患者状况
    
    @Column(name = "treatment_effect", length = 500)
    private String treatmentEffect; // 治疗效果
    
    @Column(name = "advice", length = 1000)
    private String advice; // 医生建议
    
    // 构造函数
    public FollowUp() {}
    
    // Getters and Setters
    public Visit getVisit() {
        return visit;
    }
    
    public void setVisit(Visit visit) {
        this.visit = visit;
    }
    
    public LocalDate getFollowUpDate() {
        return followUpDate;
    }
    
    public void setFollowUpDate(LocalDate followUpDate) {
        this.followUpDate = followUpDate;
    }
    
    public String getFollowUpContent() {
        return followUpContent;
    }
    
    public void setFollowUpContent(String followUpContent) {
        this.followUpContent = followUpContent;
    }
    
    public String getPatientCondition() {
        return patientCondition;
    }
    
    public void setPatientCondition(String patientCondition) {
        this.patientCondition = patientCondition;
    }
    
    public String getTreatmentEffect() {
        return treatmentEffect;
    }
    
    public void setTreatmentEffect(String treatmentEffect) {
        this.treatmentEffect = treatmentEffect;
    }
    
    public String getAdvice() {
        return advice;
    }
    
    public void setAdvice(String advice) {
        this.advice = advice;
    }
}