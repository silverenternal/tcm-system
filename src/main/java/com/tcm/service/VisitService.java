package com.tcm.service;

import com.tcm.model.Visit;
import java.util.List;
import java.util.Optional;

public interface VisitService {
    List<Visit> getAllVisits();
    Optional<Visit> getVisitById(Long id);
    Visit createVisit(Visit visit);
    Visit updateVisit(Long id, Visit visit);
    void deleteVisit(Long id);
    List<Visit> getVisitsByPatientId(Long patientId);
    List<Visit> getVisitsByDoctorId(Long doctorId);
}