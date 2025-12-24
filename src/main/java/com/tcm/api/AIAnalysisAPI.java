package com.tcm.api;

import com.tcm.service.AIAnalysisService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * AI分析与结果处理API接口
 * 接收数据整合请求，调用AI生成诊断，存储结果
 */
@RestController
@RequestMapping("/api/ai")
public class AIAnalysisAPI {

    @Autowired
    private AIAnalysisService aiAnalysisService;

    /**
     * 根据就诊ID整合数据，调用AI分析，并处理结果
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    @PostMapping("/analyze-and-process/{visitId}")
    public ResponseEntity<?> analyzeAndProcess(@PathVariable Long visitId, @RequestHeader(value = "Authorization", required = false) String authHeader) {
        try {
            Map<String, Object> aiAnalysisResult = aiAnalysisService.analyzeAndProcess(visitId);
            return ResponseEntity.ok().body(aiAnalysisResult);
        } catch (Exception e) {
            e.printStackTrace();
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Failed to call AI model");
            errorResponse.put("message", e.getMessage() != null ? e.getMessage() : "未知错误");
            return ResponseEntity.status(500).body(errorResponse);
        }
    }
}