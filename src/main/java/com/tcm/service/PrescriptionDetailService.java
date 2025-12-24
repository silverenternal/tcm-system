package com.tcm.service;

import com.tcm.model.PrescriptionDetail;
import java.util.List;
import java.util.Optional;

public interface PrescriptionDetailService {
    List<PrescriptionDetail> getAllPrescriptionDetails();
    Optional<PrescriptionDetail> getPrescriptionDetailById(Long id);
    PrescriptionDetail createPrescriptionDetail(PrescriptionDetail prescriptionDetail);
    PrescriptionDetail updatePrescriptionDetail(Long id, PrescriptionDetail prescriptionDetail);
    void deletePrescriptionDetail(Long id);
    List<PrescriptionDetail> getPrescriptionDetailsByPrescriptionId(Long prescriptionId);
}