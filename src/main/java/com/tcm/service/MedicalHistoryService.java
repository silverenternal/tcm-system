package com.tcm.service;

import com.tcm.model.MedicalHistory;
import java.util.List;
import java.util.Optional;

public interface MedicalHistoryService {
    List<MedicalHistory> getAllMedicalHistories();
    Optional<MedicalHistory> getMedicalHistoryById(Long id);
    MedicalHistory createMedicalHistory(MedicalHistory medicalHistory);
    MedicalHistory updateMedicalHistory(Long id, MedicalHistory medicalHistory);
    void deleteMedicalHistory(Long id);
    MedicalHistory findByPatientId(Long patientId);
}