package com.tcm.api;

import com.tcm.model.PrescriptionDetail;
import com.tcm.service.PrescriptionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 处方明细管理API控制器
 */
@RestController
@RequestMapping("/api/prescription-details")
public class PrescriptionDetailAPI {

    @Autowired
    private PrescriptionDetailService prescriptionDetailService;

    /**
     * 获取所有处方明细
     */
    @GetMapping
    public ResponseEntity<List<PrescriptionDetail>> getAllPrescriptionDetails() {
        List<PrescriptionDetail> prescriptionDetails = prescriptionDetailService.getAllPrescriptionDetails();
        return ResponseEntity.ok(prescriptionDetails);
    }

    /**
     * 根据ID获取处方明细
     */
    @GetMapping("/{id}")
    public ResponseEntity<PrescriptionDetail> getPrescriptionDetail(@PathVariable Long id) {
        Optional<PrescriptionDetail> prescriptionDetail = prescriptionDetailService.getPrescriptionDetailById(id);
        if (prescriptionDetail.isPresent()) {
            return ResponseEntity.ok(prescriptionDetail.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建处方明细
     */
    @PostMapping
    public ResponseEntity<PrescriptionDetail> createPrescriptionDetail(@RequestBody PrescriptionDetail prescriptionDetail) {
        PrescriptionDetail createdPrescriptionDetail = prescriptionDetailService.createPrescriptionDetail(prescriptionDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPrescriptionDetail);
    }

    /**
     * 更新处方明细
     */
    @PutMapping("/{id}")
    public ResponseEntity<PrescriptionDetail> updatePrescriptionDetail(@PathVariable Long id, @RequestBody PrescriptionDetail prescriptionDetail) {
        PrescriptionDetail updatedPrescriptionDetail = prescriptionDetailService.updatePrescriptionDetail(id, prescriptionDetail);
        if (updatedPrescriptionDetail != null) {
            return ResponseEntity.ok(updatedPrescriptionDetail);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除处方明细
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePrescriptionDetail(@PathVariable Long id) {
        prescriptionDetailService.deletePrescriptionDetail(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据处方ID获取处方明细
     */
    @GetMapping("/prescription/{prescriptionId}")
    public ResponseEntity<List<PrescriptionDetail>> getPrescriptionDetailsByPrescriptionId(@PathVariable Long prescriptionId) {
        List<PrescriptionDetail> prescriptionDetails = prescriptionDetailService.getPrescriptionDetailsByPrescriptionId(prescriptionId);
        return ResponseEntity.ok(prescriptionDetails);
    }
}