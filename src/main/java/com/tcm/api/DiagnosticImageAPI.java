package com.tcm.api;

import com.tcm.dto.DiagnosticImageResponse;
import com.tcm.model.DiagnosticImage;
import com.tcm.service.DiagnosticImageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 诊断图片API控制器
 * 用于处理舌象图片等中医诊断图片的上传和管理
 */
@RestController
@RequestMapping("/api/diagnostic-images")
public class DiagnosticImageAPI {

    @Autowired
    private DiagnosticImageService diagnosticImageService;

    /**
     * 上传诊断图片
     */
    @PostMapping("/upload")
    public ResponseEntity<DiagnosticImageResponse> uploadDiagnosticImage(
            @RequestParam("visitId") Long visitId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "imageType", defaultValue = "tongue") String imageType,
            @RequestParam(value = "description", required = false) String description) {
        try {
            DiagnosticImageResponse response = diagnosticImageService.uploadDiagnosticImage(visitId, file, imageType, description);
            return ResponseEntity.status(HttpStatus.CREATED).body(response);
        } catch (Exception e) {
            e.printStackTrace(); // 为了调试，打印异常信息
            return ResponseEntity.badRequest().build();
        }
    }

    /**
     * 根据ID获取诊断图片
     */
    @GetMapping("/{id}")
    public ResponseEntity<DiagnosticImageResponse> getDiagnosticImage(@PathVariable Long id) {
        DiagnosticImageResponse response = diagnosticImageService.getDiagnosticImageById(id);
        if (response != null) {
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 根据就诊ID获取诊断图片列表
     */
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<DiagnosticImageResponse>> getDiagnosticImagesByVisitId(@PathVariable Long visitId) {
        List<DiagnosticImageResponse> responses = diagnosticImageService.getDiagnosticImagesByVisitId(visitId);
        return ResponseEntity.ok(responses);
    }

    /**
     * 根据图片类型获取诊断图片列表
     */
    @GetMapping("/type/{imageType}")
    public ResponseEntity<List<DiagnosticImageResponse>> getDiagnosticImagesByType(@PathVariable String imageType) {
        List<DiagnosticImageResponse> responses = diagnosticImageService.getDiagnosticImagesByImageType(imageType);
        return ResponseEntity.ok(responses);
    }

    /**
     * 获取所有诊断图片列表
     */
    @GetMapping
    public ResponseEntity<List<DiagnosticImageResponse>> getAllDiagnosticImages() {
        List<DiagnosticImageResponse> responses = diagnosticImageService.getAllDiagnosticImages();
        return ResponseEntity.ok(responses);
    }

    /**
     * 删除诊断图片
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDiagnosticImage(@PathVariable Long id) {
        diagnosticImageService.deleteDiagnosticImage(id);
        return ResponseEntity.noContent().build();
    }
}