package com.tcm.api;

import com.tcm.model.MedicalHistory;
import com.tcm.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 病史记录管理API控制器
 */
@RestController
@RequestMapping("/api/medical-histories")
public class MedicalHistoryAPI {

    @Autowired
    private MedicalHistoryService medicalHistoryService;

    /**
     * 获取所有病史记录
     */
    @GetMapping
    public ResponseEntity<List<MedicalHistory>> getAllMedicalHistories() {
        List<MedicalHistory> medicalHistories = medicalHistoryService.getAllMedicalHistories();
        return ResponseEntity.ok(medicalHistories);
    }

    /**
     * 根据ID获取病史记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicalHistory> getMedicalHistory(@PathVariable Long id) {
        Optional<MedicalHistory> medicalHistory = medicalHistoryService.getMedicalHistoryById(id);
        if (medicalHistory.isPresent()) {
            return ResponseEntity.ok(medicalHistory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建病史记录
     */
    @PostMapping
    public ResponseEntity<MedicalHistory> createMedicalHistory(@RequestBody MedicalHistory medicalHistory) {
        MedicalHistory createdMedicalHistory = medicalHistoryService.createMedicalHistory(medicalHistory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedicalHistory);
    }

    /**
     * 更新病史记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicalHistory> updateMedicalHistory(@PathVariable Long id, @RequestBody MedicalHistory medicalHistory) {
        MedicalHistory updatedMedicalHistory = medicalHistoryService.updateMedicalHistory(id, medicalHistory);
        if (updatedMedicalHistory != null) {
            return ResponseEntity.ok(updatedMedicalHistory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除病史记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicalHistory(@PathVariable Long id) {
        medicalHistoryService.deleteMedicalHistory(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据患者ID获取病史记录
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<MedicalHistory> getMedicalHistoryByPatientId(@PathVariable Long patientId) {
        MedicalHistory medicalHistory = medicalHistoryService.findByPatientId(patientId);
        if (medicalHistory != null) {
            return ResponseEntity.ok(medicalHistory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}