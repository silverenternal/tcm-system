package com.tcm.service;

import com.tcm.model.*;
import com.tcm.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 数据整合服务，用于查询数据库并组装成规范化的JSON格式
 */
@Service
public class DataIntegrationService {

    @Autowired
    private PatientRepository patientRepository;

    @Autowired
    private DoctorRepository doctorRepository;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;

    /**
     * 根据就诊ID (Visit ID) 整合患者、医生、就诊、处方等信息为规范化JSON
     * @param visitId 就诊记录ID
     * @return 规范化的JSON格式数据
     */
    public Map<String, Object> integrateDataByVisitId(Long visitId) {
        // 1. 查询就诊记录
        Visit visit = visitRepository.findById(visitId).orElse(null);
        if (visit == null) {
            return null; // 或抛出异常
        }

        // 2. 根据就诊记录获取患者、医生、处方信息
        // 注意：需要检查关联对象是否为null
        Long patientId = null;
        if (visit.getPatient() != null) {
            patientId = visit.getPatient().getId();
        }

        Long doctorId = null;
        if (visit.getDoctor() != null) {
            doctorId = visit.getDoctor().getId();
        }

        List<Prescription> prescriptions = prescriptionRepository.findByVisitId(visitId);

        Patient patient = null;
        if (patientId != null) {
            patient = patientRepository.findById(patientId).orElse(null);
        }

        Doctor doctor = null;
        if (doctorId != null) {
            doctor = doctorRepository.findById(doctorId).orElse(null);
        }

        // 3. 构建规范化的JSON结构
        Map<String, Object> result = new HashMap<>();
        result.put("患者信息", buildPatientInfo(patient));
        result.put("临床表现", buildClinicalInfo(visit)); // 这里暂时用Visit信息，实际可能需要更详细的临床表现记录
        // 注意：最终结果、中医病名、证型推理、治则治法、处方名称、处方组成、编号 这些字段
        // 通常来自于 AI 分析或医生录入的诊断，而不是纯粹的数据库基础信息。
        // 我们可以预留一个字段或方法来合并这些高级信息。
        // 这里我们先基于处方信息填充部分字段
        result.put("最终结果", buildFinalResult(prescriptions, doctor));
        result.put("中医病名", "待AI分析确定或医生录入"); // 通常需要AI或医生录入
        result.put("证型推理", "待AI分析确定或医生录入"); // 通常需要AI或医生录入
        result.put("治则治法", "待AI分析确定或医生录入"); // 通常需要AI或医生录入
        result.put("编号", visitId); // 使用就诊ID作为编号

        return result;
    }

    /**
     * 构建患者信息
     */
    private Map<String, Object> buildPatientInfo(Patient patient) {
        Map<String, Object> patientInfo = new HashMap<>();
        if (patient != null) {
            patientInfo.put("姓名", patient.getName());
            patientInfo.put("性别", patient.getGender() != null ? (patient.getGender() == 1 ? "男" : "女") : "未知");
            patientInfo.put("年龄", patient.getAge() != null ? patient.getAge() + "岁" : "未知");
        }
        return patientInfo;
    }

    /**
     * 构建临床表现信息 (这里基于Visit，实际可能需要ClinicalManifestation实体)
     */
    private Map<String, Object> buildClinicalInfo(Visit visit) {
        Map<String, Object> clinicalInfo = new HashMap<>();
        if (visit != null) {
            // 这里只是示例，实际的临床表现通常比Visit的字段更多更详细
            clinicalInfo.put("症状体征", visit.getChiefComplaint() != null ? visit.getChiefComplaint() : "无记录");
            clinicalInfo.put("舌象", visit.getTongueDiagnosis() != null ? visit.getTongueDiagnosis() : "未记录");
            clinicalInfo.put("脉象", visit.getPulseDiagnosis() != null ? visit.getPulseDiagnosis() : "未记录");
            // Visit实体中没有直接的"西医检查"字段，使用初诊/复诊临床表现作为替代或补充
            String westernExamination = "";
            if (visit.getInitialVisitClinicalManifestation() != null && !visit.getInitialVisitClinicalManifestation().isEmpty()) {
                westernExamination += "初诊: " + visit.getInitialVisitClinicalManifestation();
            }
            if (visit.getFollowUpClinicalManifestation() != null && !visit.getFollowUpClinicalManifestation().isEmpty()) {
                if (!westernExamination.isEmpty()) westernExamination += "; ";
                westernExamination += "复诊: " + visit.getFollowUpClinicalManifestation();
            }
            clinicalInfo.put("西医检查", !westernExamination.isEmpty() ? westernExamination : "未记录");
            clinicalInfo.put("西医诊断", visit.getWesternDiagnosis() != null ? visit.getWesternDiagnosis() : "未诊断");
        }
        return clinicalInfo;
    }

    /**
     * 构建最终结果 (基于处方信息)
     */
    private Map<String, Object> buildFinalResult(List<Prescription> prescriptions, Doctor doctor) {
        Map<String, Object> finalResult = new HashMap<>();
        if (!prescriptions.isEmpty()) {
            Prescription firstPrescription = prescriptions.get(0); // 假设取第一个处方
            finalResult.put("处方名称", firstPrescription.getPrescriptionName() != null ? firstPrescription.getPrescriptionName() : "未命名处方");
            // 整合所有处方明细中的药材名称
            StringBuilder medicineNames = new StringBuilder();
            for (int i = 0; i < prescriptions.size(); i++) {
                if (i > 0) medicineNames.append(", ");
                List<PrescriptionDetail> details = prescriptionDetailRepository.findByPrescriptionId(prescriptions.get(i).getId());
                for (int j = 0; j < details.size(); j++) {
                    if (j > 0) medicineNames.append(", ");
                    medicineNames.append(details.get(j).getHerbName());
                }
            }
            finalResult.put("处方组成", medicineNames.toString());
        }
        return finalResult;
    }
}