package com.tcm.repository;

import com.tcm.model.Patient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PatientRepository extends JpaRepository<Patient, Long> {
    Patient findByIdCard(String idCard);

    // 添加根据自诊ID格式查找的方法
    List<Patient> findByIdCardStartingWith(String prefix);
    Patient findByPhone(String phone);
}