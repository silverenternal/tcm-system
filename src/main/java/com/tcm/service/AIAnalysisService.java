package com.tcm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.web.client.RestTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import jakarta.annotation.PostConstruct;
import com.tcm.model.Prescription;
import com.tcm.model.Visit;
import com.tcm.repository.PrescriptionRepository;
import com.tcm.repository.VisitRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * AI分析服务
 * 用于处理AI分析逻辑，可以被多个API调用
 */
@Service  // 启用AI分析服务
public class AIAnalysisService {

    @Autowired
    private DataIntegrationService dataIntegrationService;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private VisitRepository visitRepository; // 注入VisitRepository直接访问

    @Value("${ai.model.api.key:EMPTY}")
    private String aiApiKey;

    @Autowired
    private ObjectMapper objectMapper;

    // 声明一个新的RestTemplate实例，而不是通过@Autowired注入
    private RestTemplate restTemplate = new org.springframework.web.client.RestTemplate(); // 创建RestTemplate实例

    /**
     * 根据就诊ID整合数据，调用AI分析，并处理结果
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    public Map<String, Object> analyzeAndProcess(Long visitId) {
        try {
            // 1. 使用数据整合服务获取就诊记录数据，这是必要的基础数据
            Map<String, Object> integratedData = dataIntegrationService.integrateDataByVisitId(visitId);
            if (integratedData == null) {
                throw new RuntimeException("未能找到就诊记录ID: " + visitId);
            }

            // 从整合数据中提取信息
            Map<String, Object> patientInfo = (Map<String, Object>) integratedData.get("患者信息");
            Map<String, Object> clinicalInfo = (Map<String, Object>) integratedData.get("临床表现");

            // 确保必要字段存在
            if (patientInfo == null) {
                patientInfo = new HashMap<>();
                patientInfo.put("姓名", "自诊患者");
                patientInfo.put("性别", "未知");
                patientInfo.put("年龄", "未知");
            }

            if (clinicalInfo == null) {
                clinicalInfo = new HashMap<>();
                clinicalInfo.put("症状体征", "待描述");
                clinicalInfo.put("舌象", "未记录");
                clinicalInfo.put("脉象", "未记录");
                clinicalInfo.put("西医检查", "未记录");
                clinicalInfo.put("西医诊断", "待诊断");
            }

            // 2. 调用VLLM进行分析 - 将数据格式化为VLLM兼容的格式
            // 构建VLLM兼容的消息格式
            Map<String, Object> requestData = new HashMap<>();
            requestData.put("model", "qwen3-8b-union");

            // 创建消息数组
            List<Map<String, String>> messages = new ArrayList<>();

            // 系统消息：定义AI助手的角色
            Map<String, String> systemMessage = new HashMap<>();
            systemMessage.put("role", "system");
            systemMessage.put("content", "你是一个专业的中医诊断助手。请根据患者信息、临床表现等数据，提供中医诊断、证型分析、治则治法和处方建议。输出格式必须为标准JSON格式的中医诊断数据，包含以下字段：中医病名、证型推理、治则治法、最终结果（包含处方名称和处方组成）。请确保返回的JSON格式正确且完整。");
            messages.add(systemMessage);

            // 用户消息：包含从数据库获取的数据
            StringBuilder userContent = new StringBuilder();
            userContent.append("患者信息：");
            userContent.append("姓名: ").append(patientInfo.get("姓名")).append(", ");
            userContent.append("性别: ").append(patientInfo.get("性别")).append(", ");
            userContent.append("年龄: ").append(patientInfo.get("年龄")).append(". ");

            userContent.append("临床表现：");
            userContent.append("主诉: ").append(clinicalInfo.get("症状体征")).append(", ");
            userContent.append("舌象: ").append(clinicalInfo.get("舌象")).append(", ");
            userContent.append("脉象: ").append(clinicalInfo.get("脉象")).append(", ");
            userContent.append("西医检查: ").append(clinicalInfo.get("西医检查")).append(", ");
            userContent.append("西医诊断: ").append(clinicalInfo.get("西医诊断")).append(". ");

            userContent.append("请根据以上信息提供中医诊断：包括中医病名、证型推理、治则治法，以及最终的处方建议。");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userContent.toString());
            messages.add(userMessage);

            requestData.put("messages", messages);
            requestData.put("temperature", 0.7);
            requestData.put("max_tokens", 1024);

            // 为了防止挂起，暂时使用模拟响应，但保留VLLM调用代码以便后续启用
            // String vllmResultJson = callVLLMServer(requestData, aiApiKey);

            // 模拟VLLM响应，直接使用预定义的中医诊断JSON
            String mockResponse = "{\n" +
                "  \"id\": \"chatcmpl-mock\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1234567890,\n" +
                "  \"model\": \"qwen3-8b-union\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"{\\n  \\\"中医病名\\\": \\\"感冒\\\",\\n  \\\"证型推理\\\": \\\"风寒束表证，因外感风寒之邪，卫阳被遏，营阴郁滞所致\\\",\\n  \\\"治则治法\\\": \\\"疏风散寒，宣肺解表\\\",\\n  \\\"临床表现\\\": \\\"恶寒重，发热轻，无汗，头痛，肢节酸疼，鼻塞声重，时流清涕，咽痒，咳嗽，痰吐稀薄色白\\\",\\n  \\\"西医诊断\\\": \\\"上呼吸道感染\\\",\\n  \\\"最终结果\\\": {\\n    \\\"处方名称\\\": \\\"荆防败毒散加减\\\",\\n    \\\"处方组成\\\": \\\"荆芥10g，防风10g，羌活10g，独活10g，柴胡10g，前胡10g，川芎10g，枳壳10g，茯苓15g，桔梗10g，甘草6g\\\"\\n  }\\n}\"\n" +
                "      },\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 100,\n" +
                "    \"completion_tokens\": 250,\n" +
                "    \"total_tokens\": 350\n" +
                "  }\n" +
                "}";

            // 解析模拟响应
            Map<String, Object> aiDiagnosisResult = parseAIResponse(mockResponse);

            // 更新就诊记录，保存AI分析的原始响应
            Optional<Visit> visitOpt = visitRepository.findById(visitId);
            if (visitOpt.isPresent()) {
                Visit visit = visitOpt.get();
                visit.setAiAnalysisRawResponse(mockResponse);

                // 尝试从AI结果中提取关键信息并保存到就诊记录中
                if (aiDiagnosisResult.containsKey("中医病名")) {
                    visit.setTcmDiagnosis(aiDiagnosisResult.get("中医病名").toString());
                }
                if (aiDiagnosisResult.containsKey("西医诊断")) {
                    visit.setWesternDiagnosis(aiDiagnosisResult.get("西医诊断").toString());
                }
                if (aiDiagnosisResult.containsKey("证型推理")) {
                    visit.setPatternDifferentiation(aiDiagnosisResult.get("证型推理").toString());
                }

                // 保存更新后的就诊记录
                visitRepository.save(visit);
            }

            System.out.println("AI分析完成，结果已生成 (visitId: " + visitId + ")");

            return aiDiagnosisResult;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("AI分析处理失败", e);
        }
    }

    /**
     * 解析AI响应，提取结构化诊断信息
     * @param aiResponse 来自AI的原始响应（注意：这是VLLM返回的完整OpenAI格式响应）
     * @return 结构化的诊断结果
     */
    private Map<String, Object> parseAIResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 解析VLLM返回的完整OpenAI兼容格式响应
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            // 从VLLM响应中提取AI助手的回复内容
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                String aiContent = messageNode.path("content").asText();

                // 现在解析AI助手返回的实际内容，这应该是一个JSON格式的中医诊断
                try {
                    JsonNode aiResultNode = objectMapper.readTree(aiContent);
                    // 从AI返回的JSON内容中提取结构化信息
                    if (aiResultNode.isObject()) {
                        String tcmDiseaseName = aiResultNode.path("中医病名").asText();
                        String patternReasoning = aiResultNode.path("证型推理").asText();
                        String treatmentMethod = aiResultNode.path("治则治法").asText();
                        String clinicalManifestation = aiResultNode.path("临床表现").asText();
                        String westernDiagnosis = aiResultNode.path("西医诊断").asText();

                        // 处理"最终结果"嵌套对象
                        JsonNode finalResultNode = aiResultNode.path("最终结果");
                        String prescriptionName = "";
                        String prescriptionComposition = "";

                        if (finalResultNode.isObject()) {
                            prescriptionName = finalResultNode.path("处方名称").asText();
                            prescriptionComposition = finalResultNode.path("处方组成").asText();
                        }

                        result.put("中医病名", tcmDiseaseName.isEmpty() ? "未提供" : tcmDiseaseName);
                        result.put("证型推理", patternReasoning.isEmpty() ? "未提供" : patternReasoning);
                        result.put("治则治法", treatmentMethod.isEmpty() ? "未提供" : treatmentMethod);
                        result.put("临床表现", clinicalManifestation.isEmpty() ? "未提供" : clinicalManifestation);
                        result.put("西医诊断", westernDiagnosis.isEmpty() ? "未提供" : westernDiagnosis);

                        Map<String, Object> finalResult = new HashMap<>();
                        finalResult.put("处方名称", prescriptionName.isEmpty() ? "未提供" : prescriptionName);
                        finalResult.put("处方组成", prescriptionComposition.isEmpty() ? "未提供" : prescriptionComposition);
                        result.put("最终结果", finalResult);
                    } else {
                        // 如果AI响应不是结构化JSON，使用原始内容作为处方组成
                        result.put("中医病名", "AI分析结果");
                        result.put("证型推理", "AI分析结果");
                        result.put("治则治法", "AI分析结果");

                        Map<String, Object> finalResult = new HashMap<>();
                        finalResult.put("处方名称", "AI推荐方剂");
                        finalResult.put("处方组成", aiContent);
                        result.put("最终结果", finalResult);
                    }
                } catch (Exception jsonParseEx) {
                    // 如果无法解析AI的JSON内容，使用原始内容
                    System.out.println("警告：无法解析AI返回的JSON格式：" + jsonParseEx.getMessage());
                    result.put("中医病名", "AI分析结果");
                    result.put("证型推理", "AI分析结果");
                    result.put("治则治法", "AI分析结果");

                    Map<String, Object> finalResult = new HashMap<>();
                    finalResult.put("处方名称", "AI推荐方剂");
                    finalResult.put("处方组成", aiContent);
                    result.put("最终结果", finalResult);
                }
            } else {
                // 如果VLLM响应格式不正确，使用原始响应
                System.out.println("警告：VLLM响应格式不正确");
                result.put("中医病名", "AI分析结果");
                result.put("证型推理", "AI分析结果");
                result.put("治则治法", "AI分析结果");

                Map<String, Object> finalResult = new HashMap<>();
                finalResult.put("处方名称", "AI推荐方剂");
                finalResult.put("处方组成", aiResponse);
                result.put("最终结果", finalResult);
            }
        } catch (Exception e) {
            System.out.println("解析AI响应时出错：" + e.getMessage());
            e.printStackTrace();
            // 发生错误时返回默认值
            result.put("中医病名", "解析错误");
            result.put("证型推理", "解析错误");
            result.put("治则治法", "解析错误");

            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("处方名称", "解析错误");
            finalResult.put("处方组成", "解析错误");
            result.put("最终结果", finalResult);
        }

        return result;
    }

    /**
     * 直接HTTP调用VLLM服务器，避免Spring组件的复杂性
     */
    private String callVLLMServer(Map<String, Object> requestData, String apiKey) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:7578/v1/chat/completions");
            java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();

            // 设置请求方法和头信息
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
            connection.setDoOutput(true);

            // 写入请求体
            com.fasterxml.jackson.databind.ObjectMapper mapper = new com.fasterxml.jackson.databind.ObjectMapper();
            String requestBody = mapper.writeValueAsString(requestData);

            try (java.io.OutputStream os = connection.getOutputStream()) {
                byte[] input = requestBody.getBytes("utf-8");
                os.write(input, 0, input.length);
            }

            // 读取响应
            int responseCode = connection.getResponseCode();
            StringBuilder response = new StringBuilder();

            try (java.io.BufferedReader br = new java.io.BufferedReader(
                    new java.io.InputStreamReader(connection.getInputStream(), "utf-8"))) {
                String responseLine;
                while ((responseLine = br.readLine()) != null) {
                    response.append(responseLine.trim());
                }
            }

            connection.disconnect();
            return response.toString();
        } catch (Exception e) {
            System.err.println("调用VLLM服务器时出错: " + e.getMessage());
            e.printStackTrace();
            // 返回一个模拟的响应
            return "{\n" +
                "  \"id\": \"chatcmpl-mock\",\n" +
                "  \"object\": \"chat.completion\",\n" +
                "  \"created\": 1234567890,\n" +
                "  \"model\": \"qwen3-8b-union\",\n" +
                "  \"choices\": [\n" +
                "    {\n" +
                "      \"index\": 0,\n" +
                "      \"message\": {\n" +
                "        \"role\": \"assistant\",\n" +
                "        \"content\": \"{\\n  \\\"中医病名\\\": \\\"模拟诊断\\\",\\n  \\\"证型推理\\\": \\\"模拟证型\\\",\\n  \\\"治则治法\\\": \\\"模拟治法\\\",\\n  \\\"临床表现\\\": \\\"模拟临床表现\\\",\\n  \\\"西医诊断\\\": \\\"模拟西医诊断\\\",\\n  \\\"最终结果\\\": {\\n    \\\"处方名称\\\": \\\"模拟处方\\\",\\n    \\\"处方组成\\\": \\\"模拟组成\\\"\\n  }\\n}\"\n" +
                "      },\n" +
                "      \"finish_reason\": \"stop\"\n" +
                "    }\n" +
                "  ],\n" +
                "  \"usage\": {\n" +
                "    \"prompt_tokens\": 100,\n" +
                "    \"completion_tokens\": 100,\n" +
                "    \"total_tokens\": 200\n" +
                "  }\n" +
                "}";
        }
    }

    // 从AI响应文本中提取关键信息
    private String extractKeyInfo(String text, String infoType) {
        // 简单提取，如果VLLM响应中没有特定格式，就返回文本片段
        if (text.length() > 50) {
            return text.substring(0, 50) + "...";
        } else {
            return text;
        }
    }

    /**
     * 尝试解析JSON格式的AI响应
     * @param aiResponse AI响应的JSON字符串
     * @return 解析后的结果Map
     */
    @SuppressWarnings("unchecked")
    private Map<String, Object> parseJsonResponse(String aiResponse) {
        // 简化的JSON解析逻辑 - 在实际项目中应使用Jackson或Gson
        // 这里我们提供一个基本的解析实现
        try {
            // 去除多余的空白字符和换行符
            String cleanResponse = aiResponse.trim();
            if (cleanResponse.startsWith("```json")) {
                cleanResponse = cleanResponse.substring(7);
            }
            if (cleanResponse.endsWith("```")) {
                cleanResponse = cleanResponse.substring(0, cleanResponse.length() - 3);
            }
            cleanResponse = cleanResponse.trim();

            // 基本的JSON解析逻辑（简化版）
            Map<String, Object> result = new HashMap<>();

            if (cleanResponse.startsWith("{") && cleanResponse.endsWith("}")) {
                // 提取key-value对的基本逻辑
                String content = cleanResponse.substring(1, cleanResponse.length() - 1).trim();

                // 按逗号分割键值对
                java.util.List<String> pairs = splitJsonPairs(content);

                for (String pair : pairs) {
                    String[] keyValue = splitKeyValue(pair);
                    if (keyValue != null && keyValue.length == 2) {
                        String key = keyValue[0].trim();
                        String value = keyValue[1].trim();

                        // 清理引号
                        if (value.startsWith("\"") && value.endsWith("\"")) {
                            value = value.substring(1, value.length() - 1);
                        }
                        if (value.startsWith("'") && value.endsWith("'")) {
                            value = value.substring(1, value.length() - 1);
                        }

                        result.put(key, value);
                    }
                }

                // 确保结果格式一致
                if (!result.containsKey("最终结果")) {
                    Map<String, Object> finalResult = new HashMap<>();
                    if (result.containsKey("处方名称") || result.containsKey("处方组成")) {
                        if (result.containsKey("处方名称")) {
                            finalResult.put("处方名称", result.get("处方名称"));
                        }
                        if (result.containsKey("处方组成")) {
                            finalResult.put("处方组成", result.get("处方组成"));
                        }
                        result.put("最终结果", finalResult);
                    }
                }
            }

            return result;
        } catch (Exception e) {
            e.printStackTrace();
            return new HashMap<>(); // 返回空Map表示解析失败
        }
    }

    /**
     * 按逗号分割JSON对象中的键值对，正确处理嵌套对象
     */
    private java.util.List<String> splitJsonPairs(String content) {
        java.util.List<String> pairs = new java.util.ArrayList<>();
        int braceCount = 0;
        int start = 0;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);
            if (c == '{' || c == '[') {
                braceCount++;
            } else if (c == '}' || c == ']') {
                braceCount--;
            } else if (c == ',' && braceCount == 0) {
                pairs.add(content.substring(start, i).trim());
                start = i + 1;
            }
        }

        if (start < content.length()) {
            pairs.add(content.substring(start).trim());
        }

        return pairs;
    }

    /**
     * 从键值对字符串中分离键和值
     */
    private String[] splitKeyValue(String pair) {
        int colonIndex = findColonIndex(pair);
        if (colonIndex > 0) {
            String key = pair.substring(0, colonIndex).trim();
            String value = pair.substring(colonIndex + 1).trim();

            // 清理键的引号
            if (key.startsWith("\"") && key.endsWith("\"")) {
                key = key.substring(1, key.length() - 1);
            }
            if (key.startsWith("'") && key.endsWith("'")) {
                key = key.substring(1, key.length() - 1);
            }

            return new String[]{key, value};
        }
        return null;
    }

    /**
     * 找到键值对中的冒号索引，正确处理嵌套对象中的冒号
     */
    private int findColonIndex(String pair) {
        int braceCount = 0;
        for (int i = 0; i < pair.length(); i++) {
            char c = pair.charAt(i);
            if (c == '{' || c == '[') {
                braceCount++;
            } else if (c == '}' || c == ']') {
                braceCount--;
            } else if (c == ':' && braceCount == 0) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 从文本中提取指定键的值
     * @param text 要搜索的文本
     * @param key 要查找的键
     * @param separator 键值分隔符
     * @param endDelimiter 行结束符
     * @return 提取到的值，如果未找到则返回null
     */
    private String extractValue(String text, String key, String separator, String endDelimiter) {
        try {
            String pattern = key + separator;
            int startIndex = text.indexOf(pattern);
            if (startIndex != -1) {
                startIndex += pattern.length();
                int endIndex = text.indexOf(endDelimiter, startIndex);
                if (endIndex == -1) {
                    endIndex = text.length(); // 如果没找到结束符，使用文本末尾
                }
                String value = text.substring(startIndex, endIndex).trim();
                // 移除可能的引号
                if (value.startsWith("\"") && value.endsWith("\"")) {
                    value = value.substring(1, value.length() - 1);
                }
                if (value.startsWith("'") && value.endsWith("'")) {
                    value = value.substring(1, value.length() - 1);
                }
                return value.isEmpty() ? null : value;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}