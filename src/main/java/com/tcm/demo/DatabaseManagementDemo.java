package com.tcm.demo;

import com.tcm.model.Patient;
import com.tcm.model.Doctor;
import com.tcm.service.PatientService;
import com.tcm.service.DoctorService;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.time.LocalDate;

/**
 * 数据库管理系统演示应用
 * 演示独立的Java数据库管理系统功能
 */
@SpringBootApplication
@ComponentScan(basePackages = "com.tcm")
public class DatabaseManagementDemo {

    public static void main(String[] args) {
        // 启动Spring应用上下文
        ConfigurableApplicationContext context = SpringApplication.run(DatabaseManagementDemo.class, args);
        
        System.out.println("=== Java 数据库管理系统演示 ===");
        System.out.println("注意：要使此演示完全运行，需要：");
        System.out.println("1. 启动 PostgreSQL 数据库");
        System.out.println("2. 确保 application.properties 中的数据库配置正确");
        System.out.println();
        
        try {
            // 获取服务实例
            PatientService patientService = context.getBean(PatientService.class);
            DoctorService doctorService = context.getBean(DoctorService.class);
            
            // 演示创建患者
            System.out.println("1. 演示创建患者操作:");
            Patient patient = new Patient();
            patient.setName("张三");
            patient.setGender(1);
            patient.setAge(30);
            patient.setBirthDate(LocalDate.parse("1993-01-01"));
            patient.setIdCard("110101199301011234");
            patient.setPhone("13800138000");
            patient.setAddress("北京市朝阳区");
            patient.setOccupation("工程师");
            patient.setMaritalStatus(1);
            
            Patient createdPatient = patientService.createPatient(patient);
            System.out.println("   创建患者结果: " + createdPatient);
            System.out.println("   患者ID: " + createdPatient.getId());
            System.out.println();
            
            // 演示创建医生
            System.out.println("2. 演示创建医生操作:");
            Doctor doctor = new Doctor();
            doctor.setName("王医生");
            doctor.setDepartment("内科");
            doctor.setTitle("主任医师");
            doctor.setLicenseNumber("DOC123456");
            doctor.setPhone("13700137000");
            
            Doctor createdDoctor = doctorService.createDoctor(doctor);
            System.out.println("   创建医生结果: " + createdDoctor);
            System.out.println("   医生ID: " + createdDoctor.getId());
            System.out.println();
            
            // 演示获取患者
            System.out.println("3. 演示获取患者操作:");
            Patient retrievedPatient = patientService.getPatientById(createdPatient.getId()).orElse(null);
            System.out.println("   获取患者: " + retrievedPatient);
            System.out.println();
            
            // 演示获取医生
            System.out.println("4. 演示获取医生操作:");
            Doctor retrievedDoctor = doctorService.getDoctorById(createdDoctor.getId()).orElse(null);
            System.out.println("   获取医生: " + retrievedDoctor);
            System.out.println();
            
            System.out.println("=== 演示完成 ===");
            System.out.println("如需完整功能演示，请按以下步骤操作：");
            System.out.println();
            System.out.println("1. 启动PostgreSQL数据库:");
            System.out.println("   sudo systemctl start postgresql  # 或相应启动命令");
            System.out.println();
            System.out.println("2. 创建数据库:");
            System.out.println("   sudo -u postgres psql");
            System.out.println("   CREATE USER postgres WITH PASSWORD 'password';");
            System.out.println("   CREATE DATABASE tcm_hospital OWNER postgres;");
            System.out.println("   GRANT ALL PRIVILEGES ON DATABASE tcm_hospital TO postgres;");
            System.out.println();
            System.out.println("3. 启动Java服务:");
            System.out.println("   cd /path/to/java_microservices/tcm_project");
            System.out.println("   mvn spring-boot:run");
            System.out.println();
            System.out.println("4. 测试API端点:");
            System.out.println("   GET    http://localhost:58081/api/patients");
            System.out.println("   POST   http://localhost:58081/api/patients -d '{\"name\":\"测试\"}' -H \"Content-Type: application/json\"");
            System.out.println("   GET    http://localhost:58081/api/doctors");
            System.out.println("   POST   http://localhost:58081/api/doctors -d '{\"name\":\"测试医生\"}' -H \"Content-Type: application/json\"");
            
        } catch (Exception e) {
            System.err.println("演示过程中出现错误: " + e.getMessage());
            e.printStackTrace();
        } finally {
            // 关闭应用上下文
            context.close();
        }
    }
}