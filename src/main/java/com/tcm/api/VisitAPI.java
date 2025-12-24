package com.tcm.api;

import com.tcm.dto.DiagnosticImageResponse;
import com.tcm.model.Visit;
import com.tcm.service.DiagnosticImageService;
import com.tcm.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;

/**
 * 就诊记录管理API控制器
 */
@RestController
@RequestMapping("/api/visits")
public class VisitAPI {

    @Autowired
    private VisitService visitService;
    
    @Autowired
    private DiagnosticImageService diagnosticImageService;

    /**
     * 获取所有就诊记录
     */
    @GetMapping
    public ResponseEntity<List<Visit>> getAllVisits() {
        List<Visit> visits = visitService.getAllVisits();
        return ResponseEntity.ok(visits);
    }

    /**
     * 根据ID获取就诊记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<Visit> getVisit(@PathVariable Long id) {
        Optional<Visit> visit = visitService.getVisitById(id);
        if (visit.isPresent()) {
            return ResponseEntity.ok(visit.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建就诊记录
     */
    @PostMapping
    public ResponseEntity<Visit> createVisit(@RequestBody Visit visit) {
        Visit createdVisit = visitService.createVisit(visit);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdVisit);
    }

    /**
     * 更新就诊记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<Visit> updateVisit(@PathVariable Long id, @RequestBody Visit visit) {
        Visit updatedVisit = visitService.updateVisit(id, visit);
        if (updatedVisit != null) {
            return ResponseEntity.ok(updatedVisit);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除就诊记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteVisit(@PathVariable Long id) {
        visitService.deleteVisit(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据患者ID获取就诊记录
     */
    @GetMapping("/patient/{patientId}")
    public ResponseEntity<List<Visit>> getVisitsByPatientId(@PathVariable Long patientId) {
        List<Visit> visits = visitService.getVisitsByPatientId(patientId);
        return ResponseEntity.ok(visits);
    }
    
    /**
     * 根据医生ID获取就诊记录
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Visit>> getVisitsByDoctorId(@PathVariable Long doctorId) {
        List<Visit> visits = visitService.getVisitsByDoctorId(doctorId);
        return ResponseEntity.ok(visits);
    }
    
    /**
     * 上传舌象图片
     */
    @PostMapping("/{visitId}/tongue-image")
    public ResponseEntity<DiagnosticImageResponse> uploadTongueImage(
            @PathVariable Long visitId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {
        try {
            DiagnosticImageResponse response = diagnosticImageService.uploadDiagnosticImage(visitId, file, "tongue", description);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.badRequest().build();
        }
    }
}