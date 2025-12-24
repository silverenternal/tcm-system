package com.tcm.service.impl;

import com.tcm.model.PrescriptionDetail;
import com.tcm.repository.PrescriptionDetailRepository;
import com.tcm.service.PrescriptionDetailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PrescriptionDetailServiceImpl implements PrescriptionDetailService {
    
    @Autowired
    private PrescriptionDetailRepository prescriptionDetailRepository;
    
    @Override
    public List<PrescriptionDetail> getAllPrescriptionDetails() {
        return prescriptionDetailRepository.findAll();
    }
    
    @Override
    public Optional<PrescriptionDetail> getPrescriptionDetailById(Long id) {
        return prescriptionDetailRepository.findById(id);
    }
    
    @Override
    public PrescriptionDetail createPrescriptionDetail(PrescriptionDetail prescriptionDetail) {
        return prescriptionDetailRepository.save(prescriptionDetail);
    }
    
    @Override
    public PrescriptionDetail updatePrescriptionDetail(Long id, PrescriptionDetail prescriptionDetail) {
        if (prescriptionDetailRepository.existsById(id)) {
            prescriptionDetail.setId(id);
            return prescriptionDetailRepository.save(prescriptionDetail);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deletePrescriptionDetail(Long id) {
        prescriptionDetailRepository.deleteById(id);
    }
    
    @Override
    public List<PrescriptionDetail> getPrescriptionDetailsByPrescriptionId(Long prescriptionId) {
        return prescriptionDetailRepository.findByPrescriptionId(prescriptionId);
    }
}