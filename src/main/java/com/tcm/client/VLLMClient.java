package com.tcm.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import java.util.Map;

/**
 * VLLM兼容的AI大模型客户端
 * 使用标准的OpenAI兼容接口与VLLM服务通信
 */
@FeignClient(name = "vllm-service", url = "${ai.model.service.url:http://localhost:7578}")
public interface VLLMClient {

    /**
     * 聊天完成接口 - VLLM标准接口
     */
    @PostMapping(value = "/v1/chat/completions", headers = "Content-Type=application/json")
    Map<String, Object> chatCompletions(@RequestHeader("Authorization") String authHeader, @RequestBody Map<String, Object> requestData);
}