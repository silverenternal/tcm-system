package com.tcm.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

import java.time.LocalDate;

/**
 * 患者实体类
 */
@Entity
@Table(name = "patients")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Patient extends BaseEntity {
    
    @Column(name = "name", nullable = false, length = 100)
    private String name;
    
    @Column(name = "gender")
    private Integer gender; // 0-女，1-男，2-其他
    
    @Column(name = "age")
    private Integer age;
    
    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    @Column(name = "id_card", unique = false, length = 50, nullable = true)
    private String idCard;
    
    @Column(name = "phone", length = 20)
    private String phone;
    
    @Column(name = "address", length = 255)
    private String address;
    
    @Column(name = "occupation", length = 100)
    private String occupation;
    
    @Column(name = "marital_status")
    private Integer maritalStatus; // 0-未婚，1-已婚，2-离异，3-丧偶
    
    // 构造函数
    public Patient() {}
    
    // Getters and Setters
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public Integer getGender() {
        return gender;
    }
    
    public void setGender(Integer gender) {
        this.gender = gender;
    }
    
    public Integer getAge() {
        return age;
    }
    
    public void setAge(Integer age) {
        this.age = age;
    }
    
    public LocalDate getBirthDate() {
        return birthDate;
    }
    
    public void setBirthDate(LocalDate birthDate) {
        this.birthDate = birthDate;
    }
    
    public String getIdCard() {
        return idCard;
    }
    
    public void setIdCard(String idCard) {
        this.idCard = idCard;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public void setPhone(String phone) {
        this.phone = phone;
    }
    
    public String getAddress() {
        return address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public String getOccupation() {
        return occupation;
    }
    
    public void setOccupation(String occupation) {
        this.occupation = occupation;
    }
    
    public Integer getMaritalStatus() {
        return maritalStatus;
    }
    
    public void setMaritalStatus(Integer maritalStatus) {
        this.maritalStatus = maritalStatus;
    }
}