package com.tcm.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcm.model.DiagnosticImage;
import com.tcm.model.Patient;
import com.tcm.model.Visit;
import com.tcm.repository.DiagnosticImageRepository;
import com.tcm.repository.VisitRepository;
import com.tcm.service.DiagnosticImageService;
import com.tcm.service.PatientSelfDiagnosisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * 患者自诊功能API接口
 * 处理患者自诊模式下的舌象图片上传和AI分析流程
 */
@RestController
@RequestMapping("/api/self-diagnosis")
public class PatientSelfDiagnosisAPI {

    @Autowired
    private PatientSelfDiagnosisService patientSelfDiagnosisService;

    @Autowired
    private DiagnosticImageService diagnosticImageService;

    @Autowired
    private DiagnosticImageRepository diagnosticImageRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private ObjectMapper objectMapper;

    /**
     * 上传舌象图片并触发AI分析
     * @param visitId 就诊记录ID
     * @param file 舌象图片文件
     * @param description 图片描述
     * @return 上传结果和AI分析结果
     */
    @PostMapping(value = "/upload-tongue-image/{visitId}")
    public ResponseEntity<?> uploadTongueImageForSelfDiagnosis(
            @PathVariable Long visitId,
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "description", required = false) String description) {

        // 验证文件类型
        if (file.isEmpty()) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "文件不能为空");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        // 确保是图片文件
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "请选择有效的图片文件");
            return ResponseEntity.badRequest().body(errorResponse);
        }

        try {
            // 上传舌象图片
            DiagnosticImage diagnosticImage = patientSelfDiagnosisService.uploadTongueImage(visitId, file, description);

            // 触发AI分析
            Map<String, Object> aiAnalysisResult = patientSelfDiagnosisService.triggerAIAnalysis(visitId);

            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "舌象图片上传成功并已触发AI分析");
            successResponse.put("imageId", diagnosticImage.getId());
            successResponse.put("aiAnalysisResult", aiAnalysisResult);
            successResponse.put("status", "completed");
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "处理图片或AI分析失败");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 根据就诊ID获取自诊AI分析结果
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    @GetMapping("/analysis-result/{visitId}")
    public ResponseEntity<?> getSelfDiagnosisResult(@PathVariable Long visitId) {
        try {
            // 首先尝试直接从就诊记录中获取AI分析结果
            Optional<Visit> visitOpt = visitRepository.findById(visitId);
            if (!visitOpt.isPresent()) {
                Map<String, Object> errorResponse = new HashMap<>();
                errorResponse.put("error", "未找到相关就诊记录");
                return ResponseEntity.badRequest().body(errorResponse);
            }

            Visit visit = visitOpt.get();

            // 检查是否已经有AI分析结果存储在就诊记录中
            if (visit.getAiAnalysisRawResponse() != null && !visit.getAiAnalysisRawResponse().isEmpty()) {
                // 解析存储的AI分析结果
                Map<String, Object> storedResult = parseStoredAIResult(visit.getAiAnalysisRawResponse());
                if (storedResult != null) {
                    Map<String, Object> successResponse = new HashMap<>();
                    successResponse.put("status", "success");
                    successResponse.put("result", storedResult);
                    return ResponseEntity.ok(successResponse);
                }
            }

            // 如果没有存储的分析结果，尝试触发一次AI分析（仅作为备用）
            // 但在实际部署中，应该通过异步任务或手动触发来完成AI分析
            Map<String, Object> aiAnalysisResult = patientSelfDiagnosisService.getAIAnalysisResult(visitId);
            if (aiAnalysisResult != null) {
                Map<String, Object> successResponse = new HashMap<>();
                successResponse.put("status", "success");
                successResponse.put("result", aiAnalysisResult);
                return ResponseEntity.ok(successResponse);
            } else {
                // 返回空结果，表示AI分析还未完成
                Map<String, Object> pendingResponse = new HashMap<>();
                pendingResponse.put("status", "pending");
                pendingResponse.put("message", "AI analysis not completed yet");
                return ResponseEntity.ok(pendingResponse);
            }
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取AI分析结果失败");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 解析存储的AI分析结果
     * @param rawResponse 原始响应字符串
     * @return 解析后的结果Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseStoredAIResult(String rawResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(rawResponse);

            // 从完整响应中提取助手的消息内容
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                String aiContent = messageNode.path("content").asText();

                // 解析AI助手返回的实际内容
                return objectMapper.readValue(aiContent, Map.class);
            }
        } catch (Exception e) {
            System.out.println("解析存储的AI结果失败: " + e.getMessage());
        }

        return null;
    }

    /**
     * 为自诊创建就诊记录
     * @param patientId 患者ID
     * @param visitData 就诊记录数据
     * @return 创建的就诊记录
     */
    @PostMapping("/create-visit/{patientId}")
    public ResponseEntity<?> createSelfDiagnosisVisit(@PathVariable Long patientId, @RequestBody Map<String, Object> visitData) {
        try {
            Visit visit = patientSelfDiagnosisService.createSelfDiagnosisVisit(patientId, visitData);
            return ResponseEntity.ok(visit);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "创建就诊记录失败");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 完成自诊流程 - 立即返回，AI分析异步进行
     * @param visitId 就诊记录ID
     * @return 立即返回完成状态
     */
    @PostMapping("/complete-self-diagnosis/{visitId}")
    public ResponseEntity<?> completeSelfDiagnosis(@PathVariable Long visitId) {
        try {
            // 在单独线程中触发AI分析以避免API挂起
            new Thread(() -> {
                try {
                    Map<String, Object> aiAnalysisResult = patientSelfDiagnosisService.triggerAIAnalysis(visitId);
                    System.out.println("AI分析完成，visitId: " + visitId);
                } catch (Exception e) {
                    System.out.println("AI分析失败，visitId: " + visitId + ", 错误: " + e.getMessage());
                    e.printStackTrace();
                }
            }).start();

            // 立即返回响应，避免阻塞
            Map<String, Object> successResponse = new HashMap<>();
            successResponse.put("message", "自诊流程已提交，AI分析将在后台进行");
            successResponse.put("status", "submitted");
            successResponse.put("visitId", visitId);
            return ResponseEntity.ok(successResponse);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "提交自诊流程失败");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }

    /**
     * 获取指定就诊记录的所有诊断图片
     * @param visitId 就诊记录ID
     * @return 诊断图片列表
     */
    @GetMapping("/images/{visitId}")
    public ResponseEntity<?> getDiagnosticImagesByVisitId(@PathVariable Long visitId) {
        try {
            List<DiagnosticImage> images = diagnosticImageRepository.findByVisitId(visitId);
            return ResponseEntity.ok(images);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "获取诊断图片失败");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}