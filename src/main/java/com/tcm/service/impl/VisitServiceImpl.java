package com.tcm.service.impl;

import com.tcm.model.Visit;
import com.tcm.repository.VisitRepository;
import com.tcm.service.VisitService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class VisitServiceImpl implements VisitService {
    
    @Autowired
    private VisitRepository visitRepository;
    
    @Override
    public List<Visit> getAllVisits() {
        return visitRepository.findAll();
    }
    
    @Override
    public Optional<Visit> getVisitById(Long id) {
        return visitRepository.findById(id);
    }
    
    @Override
    public Visit createVisit(Visit visit) {
        return visitRepository.save(visit);
    }
    
    @Override
    public Visit updateVisit(Long id, Visit visit) {
        if (visitRepository.existsById(id)) {
            visit.setId(id);
            return visitRepository.save(visit);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deleteVisit(Long id) {
        visitRepository.deleteById(id);
    }
    
    @Override
    public List<Visit> getVisitsByPatientId(Long patientId) {
        return visitRepository.findByPatientId(patientId);
    }
    
    @Override
    public List<Visit> getVisitsByDoctorId(Long doctorId) {
        return visitRepository.findByDoctorId(doctorId);
    }
}