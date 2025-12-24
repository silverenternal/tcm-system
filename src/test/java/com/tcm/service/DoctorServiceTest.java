package com.tcm.service;

import com.tcm.model.Doctor;
import com.tcm.repository.DoctorRepository;
import com.tcm.service.impl.DoctorServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class DoctorServiceTest {

    @Mock
    private DoctorRepository doctorRepository;

    @InjectMocks
    private DoctorServiceImpl doctorService;

    private Doctor doctor;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        doctor = new Doctor();
        doctor.setId(1L);
        doctor.setName("测试医生");
        doctor.setDepartment("内科");
        doctor.setTitle("主任医师");
    }

    @Test
    void testGetAllDoctors() {
        // Given
        List<Doctor> doctors = Arrays.asList(doctor);
        when(doctorRepository.findAll()).thenReturn(doctors);

        // When
        List<Doctor> result = doctorService.getAllDoctors();

        // Then
        assertEquals(1, result.size());
        assertEquals("测试医生", result.get(0).getName());
        verify(doctorRepository, times(1)).findAll();
    }

    @Test
    void testGetDoctorById() {
        // Given
        when(doctorRepository.findById(1L)).thenReturn(Optional.of(doctor));

        // When
        Optional<Doctor> result = doctorService.getDoctorById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("测试医生", result.get().getName());
        verify(doctorRepository, times(1)).findById(1L);
    }

    @Test
    void testCreateDoctor() {
        // Given
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // When
        Doctor result = doctorService.createDoctor(doctor);

        // Then
        assertEquals("测试医生", result.getName());
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void testUpdateDoctor() {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(true);
        when(doctorRepository.save(any(Doctor.class))).thenReturn(doctor);

        // When
        Doctor result = doctorService.updateDoctor(1L, doctor);

        // Then
        assertNotNull(result);
        assertEquals("测试医生", result.getName());
        verify(doctorRepository, times(1)).existsById(1L);
        verify(doctorRepository, times(1)).save(doctor);
    }

    @Test
    void testUpdateDoctor_NotFound() {
        // Given
        when(doctorRepository.existsById(1L)).thenReturn(false);

        // When
        Doctor result = doctorService.updateDoctor(1L, doctor);

        // Then
        assertNull(result);
        verify(doctorRepository, times(1)).existsById(1L);
        verify(doctorRepository, never()).save(any(Doctor.class));
    }

    @Test
    void testDeleteDoctor() {
        // When
        doctorService.deleteDoctor(1L);

        // Then
        verify(doctorRepository, times(1)).deleteById(1L);
    }
}