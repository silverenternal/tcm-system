package com.tcm.service;

import com.tcm.model.DiagnosticImage;
import com.tcm.model.Patient;
import com.tcm.model.Visit;
import com.tcm.repository.DiagnosticImageRepository;
import com.tcm.repository.PatientRepository;
import com.tcm.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.transaction.Transactional;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

/**
 * 患者自诊业务逻辑服务
 * 处理患者自诊模式下的舌象图片上传和AI分析流程
 */
@Service
public class PatientSelfDiagnosisService {

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DiagnosticImageRepository diagnosticImageRepository;

    @Autowired
    private DataIntegrationService dataIntegrationService;

    @Autowired
    private AIAnalysisService aiAnalysisService; // 重新注入AI分析服务

    // 上传目录
    private static final String UPLOAD_DIR = "uploads/diagnostic_images/";

    /**
     * 上传舌象图片
     * @param visitId 就诊记录ID
     * @param file 图片文件
     * @param description 图片描述
     * @return 保存的诊断图片实体
     */
    @Transactional
    public DiagnosticImage uploadTongueImage(Long visitId, MultipartFile file, String description) throws IOException {
        // 验证就诊记录是否存在
        Optional<Visit> visitOpt = visitRepository.findById(visitId);
        if (!visitOpt.isPresent()) {
            throw new RuntimeException("就诊记录不存在，ID: " + visitId);
        }
        Visit visit = visitOpt.get();

        // 创建上传目录
        Path uploadPath = Paths.get(UPLOAD_DIR);
        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        // 生成文件名，首先验证文件类型
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null) {
            throw new RuntimeException("文件名不能为空");
        }

        // 确保文件名安全，防止路径遍历攻击
        String sanitizedFilename = sanitizeFilename(originalFilename);

        // 验证文件扩展名，只允许图片格式
        int extensionIndex = sanitizedFilename.lastIndexOf('.');
        if (extensionIndex == -1) {
            throw new RuntimeException("文件没有扩展名，无法识别文件类型");
        }
        String fileExtension = sanitizedFilename.substring(extensionIndex).toLowerCase();
        if (!isValidImageExtension(fileExtension)) {
            throw new RuntimeException("不支持的文件类型: " + fileExtension + ", 仅支持: jpg, jpeg, png, gif, bmp");
        }

        // 验证MIME类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("请选择有效的图片文件");
        }
        String newFileName = "tongue_" + visitId + "_" + System.currentTimeMillis() + fileExtension;

        // 保存文件，使用安全的路径
        Path filePath = uploadPath.resolve(newFileName).normalize();
        if (!filePath.startsWith(uploadPath)) {
            throw new RuntimeException("非法文件路径");
        }

        Files.write(filePath, file.getBytes());

        // 保存到数据库
        DiagnosticImage diagnosticImage = new DiagnosticImage();
        diagnosticImage.setVisit(visit);
        diagnosticImage.setImageName(newFileName);
        diagnosticImage.setImagePath(UPLOAD_DIR + newFileName);
        diagnosticImage.setImageType("tongue"); // 舌象类型
        diagnosticImage.setDescription(description != null ? description : "自诊舌象图片");
        diagnosticImage.setCreatedAt(LocalDateTime.now());

        // 设置图片的其他属性
        try {
            java.awt.image.BufferedImage img = javax.imageio.ImageIO.read(file.getInputStream());
            if (img != null) {
                diagnosticImage.setWidth(img.getWidth());
                diagnosticImage.setHeight(img.getHeight());
                diagnosticImage.setProcessedFormat(fileExtension.substring(1).toUpperCase());
            } else {
                // 如果图片无法读取，仍然保存基本信息
                System.out.println("Warning: Could not read image file, saving with basic info only");
                diagnosticImage.setWidth(0);
                diagnosticImage.setHeight(0);
                diagnosticImage.setProcessedFormat(fileExtension.substring(1).toUpperCase());
            }
        } catch (Exception e) {
            // 即使图片处理失败，也记录基本信息以确保流程继续
            System.out.println("Warning: Error processing image file: " + e.getMessage());
            diagnosticImage.setWidth(0);
            diagnosticImage.setHeight(0);
            diagnosticImage.setProcessedFormat(fileExtension.substring(1).toUpperCase());
        }

        // 始终设置文件大小
        diagnosticImage.setImageSize(file.getSize());

        return diagnosticImageRepository.save(diagnosticImage);
    }

    /**
     * 为自诊创建就诊记录
     * @param patientId 患者ID
     * @param visitData 就诊记录数据
     * @return 创建的就诊记录
     */
    @Transactional
    public Visit createSelfDiagnosisVisit(Long patientId, Map<String, Object> visitData) {
        // 验证患者是否存在
        Optional<Patient> patientOpt = patientRepository.findById(patientId);
        if (!patientOpt.isPresent()) {
            throw new RuntimeException("患者不存在，ID: " + patientId);
        }
        Patient patient = patientOpt.get();

        // 创建就诊记录
        Visit visit = new Visit();
        // 设置基本信息
        visit.setPatient(patient);

        // 从visitData中提取并设置字段
        if (visitData.containsKey("visitType")) {
            visit.setVisitType(Integer.parseInt(visitData.get("visitType").toString()));
        } else {
            visit.setVisitType(0); // 默认初诊
        }

        if (visitData.containsKey("chiefComplaint")) {
            visit.setChiefComplaint(visitData.get("chiefComplaint").toString());
        }

        if (visitData.containsKey("symptoms")) {
            visit.setSymptoms(visitData.get("symptoms").toString());
        }

        if (visitData.containsKey("treatmentPlan")) {
            visit.setTreatmentPlan(visitData.get("treatmentPlan").toString());
        }

        if (visitData.containsKey("tcmDiagnosis")) {
            visit.setTcmDiagnosis(visitData.get("tcmDiagnosis").toString());
        }

        if (visitData.containsKey("westernDiagnosis")) {
            visit.setWesternDiagnosis(visitData.get("westernDiagnosis").toString());
        }

        if (visitData.containsKey("patternDifferentiation")) {
            visit.setPatternDifferentiation(visitData.get("patternDifferentiation").toString());
        }

        if (visitData.containsKey("medicalRecordNumber")) {
            visit.setMedicalRecordNumber(visitData.get("medicalRecordNumber").toString());
        } else {
            // 自动生成病历号
            visit.setMedicalRecordNumber("SD-" + System.currentTimeMillis());
        }

        visit.setVisitDate(LocalDateTime.now());

        // 设置医生（可以选择系统默认医生或自诊医生）
        // 这里可以实现获取默认医生的逻辑
        // visit.setDoctor(defaultDoctor);

        return visitRepository.save(visit);
    }

    /**
     * 触发AI分析 - 异步方式
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    public Map<String, Object> triggerAIAnalysis(Long visitId) {
        // 通过数据整合服务获取就诊相关数据
        Map<String, Object> integratedData = dataIntegrationService.integrateDataByVisitId(visitId);
        if (integratedData == null) {
            throw new RuntimeException("未能找到就诊记录ID: " + visitId + "的相关数据");
        }

        // 启动一个新线程异步执行AI分析，避免阻塞主线程
        // 但在返回前仍需要调用一次，为了向后兼容和测试目的
        return aiAnalysisService.analyzeAndProcess(visitId);
    }

    /**
     * 获取AI分析结果
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    public Map<String, Object> getAIAnalysisResult(Long visitId) {
        // 获取就诊记录，检查是否有AI分析结果
        Optional<Visit> visitOpt = visitRepository.findById(visitId);
        if (!visitOpt.isPresent()) {
            return null;
        }

        Visit visit = visitOpt.get();

        // 准备返回结果，包含AI分析信息
        Map<String, Object> result = new HashMap<>();
        result.put("visitId", visitId);
        result.put("tcmDiagnosis", visit.getTcmDiagnosis() != null ? visit.getTcmDiagnosis() : "");
        result.put("patternDifferentiation", visit.getPatternDifferentiation() != null ? visit.getPatternDifferentiation() : "");
        result.put("treatmentPlan", visit.getTreatmentPlan() != null ? visit.getTreatmentPlan() : "");
        result.put("westernDiagnosis", visit.getWesternDiagnosis() != null ? visit.getWesternDiagnosis() : "");
        result.put("aiAnalysisRawResponse", visit.getAiAnalysisRawResponse() != null ? visit.getAiAnalysisRawResponse() : "");

        return result;
    }

    /**
     * 验证文件扩展名是否为合法图片格式
     * @param extension 文件扩展名
     * @return 是否为合法图片格式
     */
    private boolean isValidImageExtension(String extension) {
        return extension.equals(".jpg") ||
               extension.equals(".jpeg") ||
               extension.equals(".png") ||
               extension.equals(".gif") ||
               extension.equals(".bmp");
    }

    /**
     * 清理文件名，防止路径遍历攻击
     * @param filename 原始文件名
     * @return 清理后的文件名
     */
    private String sanitizeFilename(String filename) {
        // 移除路径遍历字符
        filename = filename.replace("../", "").replace("..\\", "");
        // 确保只保留文件名部分
        filename = java.nio.file.Paths.get(filename).getFileName().toString();
        return filename;
    }
}