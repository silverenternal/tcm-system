package com.tcm.service;

import com.tcm.model.Prescription;
import java.util.List;
import java.util.Optional;

public interface PrescriptionService {
    List<Prescription> getAllPrescriptions();
    Optional<Prescription> getPrescriptionById(Long id);
    Prescription createPrescription(Prescription prescription);
    Prescription updatePrescription(Long id, Prescription prescription);
    void deletePrescription(Long id);
    List<Prescription> getPrescriptionsByVisitId(Long visitId);
    List<Prescription> getPrescriptionsByDoctorId(Long doctorId);
}