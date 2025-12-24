package com.tcm.api;

import com.tcm.model.Prescription;
import com.tcm.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 处方管理API控制器
 */
@RestController
@RequestMapping("/api/prescriptions")
public class PrescriptionAPI {

    @Autowired
    private PrescriptionService prescriptionService;

    /**
     * 获取所有处方
     */
    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        List<Prescription> prescriptions = prescriptionService.getAllPrescriptions();
        return ResponseEntity.ok(prescriptions);
    }

    /**
     * 根据ID获取处方
     */
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescription(@PathVariable Long id) {
        Optional<Prescription> prescription = prescriptionService.getPrescriptionById(id);
        if (prescription.isPresent()) {
            return ResponseEntity.ok(prescription.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建处方
     */
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        Prescription createdPrescription = prescriptionService.createPrescription(prescription);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPrescription);
    }

    /**
     * 更新处方
     */
    @PutMapping("/{id}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable Long id, @RequestBody Prescription prescription) {
        Prescription updatedPrescription = prescriptionService.updatePrescription(id, prescription);
        if (updatedPrescription != null) {
            return ResponseEntity.ok(updatedPrescription);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除处方
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescription(@PathVariable Long id) {
        prescriptionService.deletePrescription(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据就诊ID获取处方
     */
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByVisitId(@PathVariable Long visitId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByVisitId(visitId);
        return ResponseEntity.ok(prescriptions);
    }
    
    /**
     * 根据医生ID获取处方
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getPrescriptionsByDoctorId(@PathVariable Long doctorId) {
        List<Prescription> prescriptions = prescriptionService.getPrescriptionsByDoctorId(doctorId);
        return ResponseEntity.ok(prescriptions);
    }
}