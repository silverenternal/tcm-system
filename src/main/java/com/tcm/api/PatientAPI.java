package com.tcm.api;

import com.tcm.model.Patient;
import com.tcm.service.PatientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 患者管理API控制器
 */
@RestController
@RequestMapping("/api/patients")
public class PatientAPI {

    @Autowired
    private PatientService patientService;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // 配置数据绑定，将空字符串转换为null
        binder.registerCustomEditor(String.class, new org.springframework.beans.propertyeditors.StringTrimmerEditor(true));
    }

    /**
     * 获取所有患者
     */
    @GetMapping
    public ResponseEntity<List<Patient>> getAllPatients() {
        List<Patient> patients = patientService.getAllPatients();
        return ResponseEntity.ok(patients);
    }

    /**
     * 根据ID获取患者
     */
    @GetMapping("/{id}")
    public ResponseEntity<Patient> getPatient(@PathVariable Long id) {
        Optional<Patient> patient = patientService.getPatientById(id);
        if (patient.isPresent()) {
            return ResponseEntity.ok(patient.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建患者
     */
    @PostMapping
    public ResponseEntity<Patient> createPatient(@RequestBody Patient patient) {
        Patient createdPatient = patientService.createPatient(patient);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdPatient);
    }

    /**
     * 更新患者
     */
    @PutMapping("/{id}")
    public ResponseEntity<Patient> updatePatient(@PathVariable Long id, @RequestBody Patient patient) {
        Patient updatedPatient = patientService.updatePatient(id, patient);
        if (updatedPatient != null) {
            return ResponseEntity.ok(updatedPatient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除患者
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePatient(@PathVariable Long id) {
        patientService.deletePatient(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据身份证号查找患者
     */
    @GetMapping("/id-card/{idCard}")
    public ResponseEntity<Patient> getPatientByIdCard(@PathVariable String idCard) {
        Patient patient = patientService.findByPatientIdCard(idCard);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据电话号码查找患者
     */
    @GetMapping("/phone/{phone}")
    public ResponseEntity<Patient> getPatientByPhone(@PathVariable String phone) {
        Patient patient = patientService.findByPatientPhone(phone);
        if (patient != null) {
            return ResponseEntity.ok(patient);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}