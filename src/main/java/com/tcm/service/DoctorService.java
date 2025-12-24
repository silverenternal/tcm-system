package com.tcm.service;

import com.tcm.model.Doctor;
import java.util.List;
import java.util.Optional;

public interface DoctorService {
    List<Doctor> getAllDoctors();
    Optional<Doctor> getDoctorById(Long id);
    Doctor createDoctor(Doctor doctor);
    Doctor updateDoctor(Long id, Doctor doctor);
    void deleteDoctor(Long id);
    Doctor findByLicenseNumber(String licenseNumber);
    Doctor findByDoctorPhone(String phone);
}