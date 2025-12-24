package com.tcm.service.impl;

import com.tcm.model.Doctor;
import com.tcm.repository.DoctorRepository;
import com.tcm.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class DoctorServiceImpl implements DoctorService {
    
    @Autowired
    private DoctorRepository doctorRepository;
    
    @Override
    public List<Doctor> getAllDoctors() {
        return doctorRepository.findAll();
    }
    
    @Override
    public Optional<Doctor> getDoctorById(Long id) {
        return doctorRepository.findById(id);
    }
    
    @Override
    public Doctor createDoctor(Doctor doctor) {
        return doctorRepository.save(doctor);
    }
    
    @Override
    public Doctor updateDoctor(Long id, Doctor doctor) {
        if (doctorRepository.existsById(id)) {
            doctor.setId(id);
            return doctorRepository.save(doctor);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deleteDoctor(Long id) {
        doctorRepository.deleteById(id);
    }
    
    @Override
    public Doctor findByLicenseNumber(String licenseNumber) {
        return doctorRepository.findByLicenseNumber(licenseNumber);
    }
    
    @Override
    public Doctor findByDoctorPhone(String phone) {
        return doctorRepository.findByPhone(phone);
    }
}