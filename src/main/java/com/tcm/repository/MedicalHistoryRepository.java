package com.tcm.repository;

import com.tcm.model.MedicalHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicalHistoryRepository extends JpaRepository<MedicalHistory, Long> {
    MedicalHistory findByPatientId(Long patientId);
}