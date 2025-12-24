package com.tcm.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.tcm.client.VLLMClient;
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
@Service
public class AIAnalysisService {

    @Autowired
    private DataIntegrationService dataIntegrationService;

    @Autowired
    private VLLMClient vllmClient;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Value("${ai.model.api.key:EMPTY}")
    private String aiApiKey;

    /**
     * 根据就诊ID整合数据，调用AI分析，并处理结果
     * @param visitId 就诊记录ID
     * @return AI分析结果
     */
    public Map<String, Object> analyzeAndProcess(Long visitId) {
        // 1. 整合数据
        Map<String, Object> integratedData = dataIntegrationService.integrateDataByVisitId(visitId);
        if (integratedData == null) {
            throw new RuntimeException("Visit not found with id: " + visitId);
        }

        // 2. 调用VLLM进行分析 - 将数据格式化为VLLM兼容的格式
        try {
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

            // 用户消息：包含从数据库整合的数据
            Map<String, Object> clinicalInfo = (Map<String, Object>) integratedData.get("临床表现");
            Map<String, Object> patientInfo = (Map<String, Object>) integratedData.get("患者信息");

            StringBuilder userContent = new StringBuilder();
            userContent.append("患者信息：");
            if (patientInfo != null) {
                userContent.append("姓名: ").append(patientInfo.get("姓名")).append(", ");
                userContent.append("性别: ").append(patientInfo.get("性别")).append(", ");
                userContent.append("年龄: ").append(patientInfo.get("年龄")).append(". ");
            }

            userContent.append("临床表现：");
            if (clinicalInfo != null) {
                userContent.append("主诉: ").append(clinicalInfo.get("症状体征")).append(", ");
                userContent.append("舌象: ").append(clinicalInfo.get("舌象")).append(", ");
                userContent.append("脉象: ").append(clinicalInfo.get("脉象")).append(", ");
                userContent.append("西医检查: ").append(clinicalInfo.get("西医检查")).append(", ");
                userContent.append("西医诊断: ").append(clinicalInfo.get("西医诊断")).append(". ");
            }

            userContent.append("请根据以上信息提供中医诊断：包括中医病名、证型推理、治则治法，以及最终的处方建议。");

            Map<String, String> userMessage = new HashMap<>();
            userMessage.put("role", "user");
            userMessage.put("content", userContent.toString());
            messages.add(userMessage);

            requestData.put("messages", messages);
            requestData.put("temperature", 0.7);
            requestData.put("max_tokens", 1024);

            // 调用VLLM API - 使用配置的API密钥
            String authHeaderValue = "Bearer " + aiApiKey; // VLLM标准认证格式
            Map<String, Object> vllmResult = vllmClient.chatCompletions(authHeaderValue, requestData);

            // 解析VLLM响应
            if (vllmResult == null || !vllmResult.containsKey("choices") ||
                ((List<?>) vllmResult.get("choices")).isEmpty()) {
                throw new RuntimeException("Invalid response from AI model");
            }

            List<Map<String, Object>> choices = (List<Map<String, Object>>) vllmResult.get("choices");
            Map<String, Object> firstChoice = choices.get(0);
            Map<String, Object> message = (Map<String, Object>) firstChoice.get("message");
            String aiResponse = (String) message.get("content");

            // 解析AI响应内容，提取结构化信息
            Map<String, Object> aiDiagnosisResult = parseAIResponse(aiResponse);

            // 3. 获取对应的就诊记录以进行更新
            Visit visit = visitRepository.findById(visitId).orElse(null);
            if (visit == null) {
                throw new RuntimeException("Visit not found with id: " + visitId + " during update.");
            }

            // 存储原始AI响应用于调试和验证
            visit.setAiAnalysisRawResponse(aiResponse);

            // 4. 解析AI结果，并更新就诊记录 (Visit)
            boolean visitUpdated = false;
            if (aiDiagnosisResult.containsKey("中医病名")) {
                visit.setTcmDiagnosis((String) aiDiagnosisResult.get("中医病名"));
                visitUpdated = true;
            }
            if (aiDiagnosisResult.containsKey("证型推理")) {
                visit.setPatternDifferentiation((String) aiDiagnosisResult.get("证型推理"));
                visitUpdated = true;
            }
            if (aiDiagnosisResult.containsKey("治则治法")) {
                visit.setTreatmentPlan((String) aiDiagnosisResult.get("治则治法"));
                visitUpdated = true;
            }
            // 添加对其他可能字段的处理，如西医诊断等
            if (aiDiagnosisResult.containsKey("西医诊断")) {
                visit.setWesternDiagnosis((String) aiDiagnosisResult.get("西医诊断"));
                visitUpdated = true;
            }
            if (aiDiagnosisResult.containsKey("临床表现")) {
                // 根据就诊类型决定更新哪个字段
                if (visit.getVisitType() != null && visit.getVisitType() == 0) { // 初诊
                    visit.setInitialVisitClinicalManifestation((String) aiDiagnosisResult.get("临床表现"));
                } else { // 复诊
                    visit.setFollowUpClinicalManifestation((String) aiDiagnosisResult.get("临床表现"));
                }
                visitUpdated = true;
            }

            if (visitUpdated) {
                try {
                    Visit savedVisit = visitRepository.save(visit); // 更新就诊记录
                    System.out.println("Visit record updated successfully with ID: " + savedVisit.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to update visit record in database", e);
                }
            } else {
                System.out.println("No updates were applied to the visit record");
            }

            // 5. 解析AI结果，并存储处方 (Prescription)
            Prescription prescription = new Prescription();
            prescription.setVisit(visit);

            boolean prescriptionUpdated = false;
            if (aiDiagnosisResult.containsKey("最终结果")) {
                Map<String, Object> finalResultMap = (Map<String, Object>) aiDiagnosisResult.get("最终结果");
                if (finalResultMap.containsKey("处方名称")) {
                    prescription.setPrescriptionName((String) finalResultMap.get("处方名称"));
                    prescriptionUpdated = true;
                }
                if (finalResultMap.containsKey("处方组成")) {
                    prescription.setDoctorAdvice((String) finalResultMap.get("处方组成"));
                    prescriptionUpdated = true;
                }
            } else {
                // 如果没有最终结果，但仍需要保存处方，可以尝试直接提取处方信息
                if (aiDiagnosisResult.containsKey("处方名称")) {
                    prescription.setPrescriptionName((String) aiDiagnosisResult.get("处方名称"));
                    prescriptionUpdated = true;
                }
                if (aiDiagnosisResult.containsKey("处方组成")) {
                    prescription.setDoctorAdvice((String) aiDiagnosisResult.get("处方组成"));
                    prescriptionUpdated = true;
                }
            }

            if (prescriptionUpdated) {
                try {
                    Prescription savedPrescription = prescriptionRepository.save(prescription); // 保存处方
                    System.out.println("Prescription created successfully with ID: " + savedPrescription.getId());
                } catch (Exception e) {
                    e.printStackTrace();
                    throw new RuntimeException("Failed to save prescription to database", e);
                }
            } else {
                System.out.println("No prescription information was available to save");
            }

            return aiDiagnosisResult;

        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to call AI model", e);
        }
    }

    /**
     * 解析AI响应，提取结构化诊断信息
     * @param aiResponse 来自AI的原始响应
     * @return 结构化的诊断结果
     */
    private Map<String, Object> parseAIResponse(String aiResponse) {
        Map<String, Object> result = new HashMap<>();

        try {
            // 直接解析VLLM返回的AI助手内容，而不是整个OpenAI响应格式
            // 因为VLLM返回的是标准OpenAI格式，AI的响应内容在choices[0].message.content中
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(aiResponse);

            // 从choices中获取AI助手的回复内容
            JsonNode choicesNode = rootNode.path("choices");
            if (choicesNode.isArray() && choicesNode.size() > 0) {
                JsonNode firstChoice = choicesNode.get(0);
                JsonNode messageNode = firstChoice.path("message");
                String content = messageNode.path("content").asText();

                // 现在解析AI返回的JSON内容，而不是整个OpenAI格式
                try {
                    JsonNode aiContentNode = objectMapper.readTree(content);
                    // 直接从AI返回的JSON内容中提取结构化信息
                    if (aiContentNode.isObject()) {
                        // 处理AI返回的结构化JSON数据
                        String tcmDiseaseName = aiContentNode.path("中医病名").asText();
                        String patternReasoning = aiContentNode.path("证型推理").asText();
                        String treatmentMethod = aiContentNode.path("治则治法").asText();
                        String clinicalManifestation = aiContentNode.path("临床表现").asText();
                        String westernDiagnosis = aiContentNode.path("西医诊断").asText();

                        // 处理"最终结果"嵌套对象
                        JsonNode finalResultNode = aiContentNode.path("最终结果");
                        String prescriptionName = "";
                        String prescriptionComposition = "";

                        if (finalResultNode.isObject()) {
                            prescriptionName = finalResultNode.path("处方名称").asText();
                            prescriptionComposition = finalResultNode.path("处方组成").asText();
                        }

                        result.put("中医病名", tcmDiseaseName.isEmpty() ? "AI正在分析中" : tcmDiseaseName);
                        result.put("证型推理", patternReasoning.isEmpty() ? "AI正在分析中" : patternReasoning);
                        result.put("治则治法", treatmentMethod.isEmpty() ? "AI正在分析中" : treatmentMethod);
                        result.put("临床表现", clinicalManifestation.isEmpty() ? "AI正在分析中" : clinicalManifestation);
                        result.put("西医诊断", westernDiagnosis.isEmpty() ? "AI正在分析中" : westernDiagnosis);

                        Map<String, Object> finalResult = new HashMap<>();
                        finalResult.put("处方名称", prescriptionName.isEmpty() ? "AI推荐方剂" : prescriptionName);
                        finalResult.put("处方组成", prescriptionComposition.isEmpty() ? content : prescriptionComposition);
                        result.put("最终结果", finalResult);
                    } else {
                        // 如果AI响应不是结构化JSON，使用原始内容
                        result.put("中医病名", "AI分析结果");
                        result.put("证型推理", "AI分析结果");
                        result.put("治则治法", "AI分析结果");

                        Map<String, Object> finalResult = new HashMap<>();
                        finalResult.put("处方名称", "AI推荐方剂");
                        finalResult.put("处方组成", content); // 使用AI原始响应内容
                        result.put("最终结果", finalResult);
                    }
                } catch (Exception jsonParseEx) {
                    // 如果无法解析AI的JSON内容，使用原始内容
                    jsonParseEx.printStackTrace();
                    result.put("中医病名", "AI分析结果");
                    result.put("证型推理", "AI分析结果");
                    result.put("治则治法", "AI分析结果");

                    Map<String, Object> finalResult = new HashMap<>();
                    finalResult.put("处方名称", "AI推荐方剂");
                    finalResult.put("处方组成", content); // 使用AI原始响应内容
                    result.put("最终结果", finalResult);
                }
            } else {
                // 如果不是标准格式，使用原始响应
                result.put("中医病名", "AI分析结果");
                result.put("证型推理", "AI分析结果");
                result.put("治则治法", "AI分析结果");

                Map<String, Object> finalResult = new HashMap<>();
                finalResult.put("处方名称", "AI推荐方剂");
                finalResult.put("处方组成", aiResponse); // 使用原始AI响应
                result.put("最终结果", finalResult);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // 如果解析失败，返回VLLM原始响应
            result.put("中医病名", "AI分析结果");
            result.put("证型推理", "AI分析结果");
            result.put("治则治法", "AI分析结果");

            Map<String, Object> finalResult = new HashMap<>();
            finalResult.put("处方名称", "AI推荐方剂");
            finalResult.put("处方组成", aiResponse); // 使用原始AI响应
            result.put("最终结果", finalResult);
        }

        return result;
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