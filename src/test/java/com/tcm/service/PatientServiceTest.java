package com.tcm.service;

import com.tcm.model.Patient;
import com.tcm.repository.PatientRepository;
import com.tcm.service.impl.PatientServiceImpl;
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

public class PatientServiceTest {

    @Mock
    private PatientRepository patientRepository;

    @InjectMocks
    private PatientServiceImpl patientService;

    private Patient patient;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        patient = new Patient();
        patient.setId(1L);
        patient.setName("测试患者");
        patient.setGender(1);
        patient.setAge(30);
    }

    @Test
    void testGetAllPatients() {
        // Given
        List<Patient> patients = Arrays.asList(patient);
        when(patientRepository.findAll()).thenReturn(patients);

        // When
        List<Patient> result = patientService.getAllPatients();

        // Then
        assertEquals(1, result.size());
        assertEquals("测试患者", result.get(0).getName());
        verify(patientRepository, times(1)).findAll();
    }

    @Test
    void testGetPatientById() {
        // Given
        when(patientRepository.findById(1L)).thenReturn(Optional.of(patient));

        // When
        Optional<Patient> result = patientService.getPatientById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("测试患者", result.get().getName());
        verify(patientRepository, times(1)).findById(1L);
    }

    @Test
    void testCreatePatient() {
        // Given
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        Patient result = patientService.createPatient(patient);

        // Then
        assertEquals("测试患者", result.getName());
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testUpdatePatient() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(true);
        when(patientRepository.save(any(Patient.class))).thenReturn(patient);

        // When
        Patient result = patientService.updatePatient(1L, patient);

        // Then
        assertNotNull(result);
        assertEquals("测试患者", result.getName());
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, times(1)).save(patient);
    }

    @Test
    void testUpdatePatient_NotFound() {
        // Given
        when(patientRepository.existsById(1L)).thenReturn(false);

        // When
        Patient result = patientService.updatePatient(1L, patient);

        // Then
        assertNull(result);
        verify(patientRepository, times(1)).existsById(1L);
        verify(patientRepository, never()).save(any(Patient.class));
    }

    @Test
    void testDeletePatient() {
        // When
        patientService.deletePatient(1L);

        // Then
        verify(patientRepository, times(1)).deleteById(1L);
    }
}