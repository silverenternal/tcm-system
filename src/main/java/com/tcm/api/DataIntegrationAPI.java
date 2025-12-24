package com.tcm.api;

import com.tcm.service.DataIntegrationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 数据整合API接口 (仅整合数据，不调用AI)
 */
@RestController
@RequestMapping("/api/integration")
public class DataIntegrationAPI {

    @Autowired
    private DataIntegrationService dataIntegrationService;

    /**
     * 仅整合数据，不调用AI
     * @param visitId 就诊记录ID
     * @return 整合后的数据
     */
    @GetMapping("/data-by-visit/{visitId}")
    public ResponseEntity<?> getDataByVisitId(@PathVariable Long visitId) {
        Map<String, Object> integratedData = dataIntegrationService.integrateDataByVisitId(visitId);
        if (integratedData == null) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("error", "Visit not found with id: " + visitId);
            return ResponseEntity.badRequest().body(errorResponse);
        }
        return ResponseEntity.ok().body(integratedData);
    }
}