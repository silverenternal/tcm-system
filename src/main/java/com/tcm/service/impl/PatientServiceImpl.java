package com.tcm.service.impl;

import com.tcm.model.Patient;
import com.tcm.repository.PatientRepository;
import com.tcm.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PatientServiceImpl implements PatientService {
    
    @Autowired
    private PatientRepository patientRepository;
    
    @Override
    public List<Patient> getAllPatients() {
        return patientRepository.findAll();
    }
    
    @Override
    public Optional<Patient> getPatientById(Long id) {
        return patientRepository.findById(id);
    }
    
    @Override
    public Patient createPatient(Patient patient) {
        // 强制确保idCard字段不为空字符串，即使前端发送空字符串也转为null
        if (patient.getIdCard() != null) {
            String trimmedIdCard = patient.getIdCard().trim();
            if (trimmedIdCard.isEmpty()) {
                patient.setIdCard(null);
            } else if (trimmedIdCard.length() > 18) {
                // 防止意外的超长ID卡号
                patient.setIdCard(trimmedIdCard.substring(0, 18));
            } else {
                patient.setIdCard(trimmedIdCard);
            }
        }

        // 添加调试日志
        System.out.println("DEBUG: Before save - idCard: '" + patient.getIdCard() + "', length: " + (patient.getIdCard() != null ? patient.getIdCard().length() : "null"));

        // 同样处理其他可能为空的字符串字段
        if (patient.getPhone() != null) {
            String trimmedPhone = patient.getPhone().trim();
            patient.setPhone(trimmedPhone.isEmpty() ? null : trimmedPhone);
        }
        if (patient.getAddress() != null) {
            String trimmedAddress = patient.getAddress().trim();
            patient.setAddress(trimmedAddress.isEmpty() ? null : trimmedAddress);
        }
        if (patient.getOccupation() != null) {
            String trimmedOccupation = patient.getOccupation().trim();
            patient.setOccupation(trimmedOccupation.isEmpty() ? null : trimmedOccupation);
        }

        Patient savedPatient = patientRepository.save(patient);
        System.out.println("DEBUG: After save - idCard: '" + savedPatient.getIdCard() + "'");
        return savedPatient;
    }
    
    @Override
    public Patient updatePatient(Long id, Patient patient) {
        if (patientRepository.existsById(id)) {
            // 强制确保idCard字段不为空字符串，即使前端发送空字符串也转为null
            if (patient.getIdCard() != null) {
                String trimmedIdCard = patient.getIdCard().trim();
                if (trimmedIdCard.isEmpty()) {
                    // 如果是空字符串，则保持现有ID卡号或设为null
                    Patient existingPatient = patientRepository.findById(id).orElse(null);
                    if (existingPatient != null) {
                        patient.setIdCard(existingPatient.getIdCard());
                    } else {
                        patient.setIdCard(null);
                    }
                } else if (trimmedIdCard.length() > 18) {
                    // 防止意外的超长ID卡号
                    patient.setIdCard(trimmedIdCard.substring(0, 18));
                } else {
                    patient.setIdCard(trimmedIdCard);
                }
            }

            if (patient.getPhone() != null) {
                String trimmedPhone = patient.getPhone().trim();
                patient.setPhone(trimmedPhone.isEmpty() ? null : trimmedPhone);
            }
            if (patient.getAddress() != null) {
                String trimmedAddress = patient.getAddress().trim();
                patient.setAddress(trimmedAddress.isEmpty() ? null : trimmedAddress);
            }
            if (patient.getOccupation() != null) {
                String trimmedOccupation = patient.getOccupation().trim();
                patient.setOccupation(trimmedOccupation.isEmpty() ? null : trimmedOccupation);
            }

            patient.setId(id);
            return patientRepository.save(patient);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deletePatient(Long id) {
        patientRepository.deleteById(id);
    }
    
    @Override
    public Patient findByPatientIdCard(String idCard) {
        return patientRepository.findByIdCard(idCard);
    }
    
    @Override
    public Patient findByPatientPhone(String phone) {
        return patientRepository.findByPhone(phone);
    }
}