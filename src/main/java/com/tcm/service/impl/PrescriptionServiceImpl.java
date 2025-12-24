package com.tcm.service.impl;

import com.tcm.model.Prescription;
import com.tcm.repository.PrescriptionRepository;
import com.tcm.service.PrescriptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionServiceImpl implements PrescriptionService {
    
    @Autowired
    private PrescriptionRepository prescriptionRepository;
    
    @Override
    public List<Prescription> getAllPrescriptions() {
        return prescriptionRepository.findAll();
    }
    
    @Override
    public Optional<Prescription> getPrescriptionById(Long id) {
        return prescriptionRepository.findById(id);
    }
    
    @Override
    public Prescription createPrescription(Prescription prescription) {
        return prescriptionRepository.save(prescription);
    }
    
    @Override
    public Prescription updatePrescription(Long id, Prescription prescription) {
        if (prescriptionRepository.existsById(id)) {
            prescription.setId(id);
            return prescriptionRepository.save(prescription);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deletePrescription(Long id) {
        prescriptionRepository.deleteById(id);
    }
    
    @Override
    public List<Prescription> getPrescriptionsByVisitId(Long visitId) {
        return prescriptionRepository.findByVisitId(visitId);
    }
    
    @Override
    public List<Prescription> getPrescriptionsByDoctorId(Long doctorId) {
        return prescriptionRepository.findByDoctorId(doctorId);
    }
}