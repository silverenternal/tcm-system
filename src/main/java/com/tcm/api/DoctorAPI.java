package com.tcm.api;

import com.tcm.model.Doctor;
import com.tcm.service.DoctorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 医生管理API控制器
 */
@RestController
@RequestMapping("/api/doctors")
public class DoctorAPI {

    @Autowired
    private DoctorService doctorService;

    /**
     * 获取所有医生
     */
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors() {
        List<Doctor> doctors = doctorService.getAllDoctors();
        return ResponseEntity.ok(doctors);
    }

    /**
     * 根据ID获取医生
     */
    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getDoctor(@PathVariable Long id) {
        Optional<Doctor> doctor = doctorService.getDoctorById(id);
        if (doctor.isPresent()) {
            return ResponseEntity.ok(doctor.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建医生
     */
    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor createdDoctor = doctorService.createDoctor(doctor);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdDoctor);
    }

    /**
     * 更新医生
     */
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> updateDoctor(@PathVariable Long id, @RequestBody Doctor doctor) {
        Doctor updatedDoctor = doctorService.updateDoctor(id, doctor);
        if (updatedDoctor != null) {
            return ResponseEntity.ok(updatedDoctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除医生
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDoctor(@PathVariable Long id) {
        doctorService.deleteDoctor(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据执业资格证号查找医生
     */
    @GetMapping("/license/{licenseNumber}")
    public ResponseEntity<Doctor> getDoctorByLicenseNumber(@PathVariable String licenseNumber) {
        Doctor doctor = doctorService.findByLicenseNumber(licenseNumber);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据电话号码查找医生
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<Doctor> getDoctorByPhone(@PathVariable String phone) {
        Doctor doctor = doctorService.findByDoctorPhone(phone);
        if (doctor != null) {
            return ResponseEntity.ok(doctor);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}