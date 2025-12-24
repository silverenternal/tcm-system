package com.tcm.repository;

import com.tcm.model.Doctor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DoctorRepository extends JpaRepository<Doctor, Long> {
    Doctor findByLicenseNumber(String licenseNumber);
    Doctor findByPhone(String phone);
}