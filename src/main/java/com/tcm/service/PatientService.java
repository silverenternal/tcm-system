package com.tcm.service;

import com.tcm.model.Patient;
import java.util.List;
import java.util.Optional;

public interface PatientService {
    List<Patient> getAllPatients();
    Optional<Patient> getPatientById(Long id);
    Patient createPatient(Patient patient);
    Patient updatePatient(Long id, Patient patient);
    void deletePatient(Long id);
    Patient findByPatientIdCard(String idCard);
    Patient findByPatientPhone(String phone);
}