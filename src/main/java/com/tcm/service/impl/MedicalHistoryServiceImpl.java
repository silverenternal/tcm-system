package com.tcm.service.impl;

import com.tcm.model.MedicalHistory;
import com.tcm.repository.MedicalHistoryRepository;
import com.tcm.service.MedicalHistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicalHistoryServiceImpl implements MedicalHistoryService {
    
    @Autowired
    private MedicalHistoryRepository medicalHistoryRepository;
    
    @Override
    public List<MedicalHistory> getAllMedicalHistories() {
        return medicalHistoryRepository.findAll();
    }
    
    @Override
    public Optional<MedicalHistory> getMedicalHistoryById(Long id) {
        return medicalHistoryRepository.findById(id);
    }
    
    @Override
    public MedicalHistory createMedicalHistory(MedicalHistory medicalHistory) {
        return medicalHistoryRepository.save(medicalHistory);
    }
    
    @Override
    public MedicalHistory updateMedicalHistory(Long id, MedicalHistory medicalHistory) {
        if (medicalHistoryRepository.existsById(id)) {
            medicalHistory.setId(id);
            return medicalHistoryRepository.save(medicalHistory);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deleteMedicalHistory(Long id) {
        medicalHistoryRepository.deleteById(id);
    }
    
    @Override
    public MedicalHistory findByPatientId(Long patientId) {
        return medicalHistoryRepository.findByPatientId(patientId);
    }
}