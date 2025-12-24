package com.tcm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;

/**
 * AI大模型处理端客户端
 * 通过Feign实现与AI大模型服务的通信
 */
@FeignClient(name = "ai-model-service", url = "${ai.model.service.url:http://localhost:8082}")
public interface AIBigModelClient {
    
    /**
     * 文本处理接口
     */
    @PostMapping(value = "/text/process", headers = "Content-Type=application/json")
    Map<String, Object> processText(@RequestBody Map<String, String> textData);
    
    /**
     * 图像处理接口
     */
    @PostMapping(value = "/image/process", consumes = "multipart/form-data")
    Map<String, Object> processImage(@RequestPart("image") MultipartFile image);
    
    /**
     * 语音处理接口
     */
    @PostMapping(value = "/audio/process", consumes = "multipart/form-data")
    Map<String, Object> processAudio(@RequestPart("audio") MultipartFile audio);
    
    /**
     * AI对话接口
     */
    @PostMapping(value = "/conversation", headers = "Content-Type=application/json")
    Map<String, Object> conversation(@RequestBody Map<String, String> conversationData);
    
    /**
     * 获取AI模型服务状态
     */
    @GetMapping("/status")
    Map<String, Object> getAIModelStatus();
    
    /**
     * 批量处理接口
     */
    @PostMapping(value = "/batch/process", headers = "Content-Type=application/json")
    Map<String, Object> batchProcess(@RequestBody Map<String, Object> batchData);

    /**
     * 分析患者数据并生成诊断方案
     * @param inputData 包含患者、医生、就诊、处方等信息的规范化JSON
     * @return AI生成的诊断方案
     */
    @PostMapping(value = "/diagnosis/analyze", headers = "Content-Type=application/json")
    Map<String, Object> analyzeDiagnosis(@RequestBody Map<String, Object> inputData);
}