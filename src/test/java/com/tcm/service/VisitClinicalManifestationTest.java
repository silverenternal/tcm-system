package com.tcm.service;

import com.tcm.model.Visit;
import com.tcm.repository.VisitRepository;
import com.tcm.service.impl.VisitServiceImpl;
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

public class VisitClinicalManifestationTest {

    @Mock
    private VisitRepository visitRepository;

    @InjectMocks
    private VisitServiceImpl visitService;

    private Visit visit;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        visit = new Visit();
        visit.setId(1L);
        visit.setInitialVisitClinicalManifestation("初诊临床表现：血压偏高，头晕头痛明显");
        visit.setFollowUpClinicalManifestation("复诊临床表现：症状有所缓解");
    }

    @Test
    void testCreateVisitWithClinicalManifestations() {
        // Given
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);

        // When
        Visit result = visitService.createVisit(visit);

        // Then
        assertEquals("初诊临床表现：血压偏高，头晕头痛明显", result.getInitialVisitClinicalManifestation());
        assertEquals("复诊临床表现：症状有所缓解", result.getFollowUpClinicalManifestation());
        verify(visitRepository, times(1)).save(visit);
    }

    @Test
    void testUpdateVisitWithClinicalManifestations() {
        // Given
        when(visitRepository.existsById(1L)).thenReturn(true);
        when(visitRepository.save(any(Visit.class))).thenReturn(visit);

        // When
        Visit result = visitService.updateVisit(1L, visit);

        // Then
        assertNotNull(result);
        assertEquals("初诊临床表现：血压偏高，头晕头痛明显", result.getInitialVisitClinicalManifestation());
        assertEquals("复诊临床表现：症状有所缓解", result.getFollowUpClinicalManifestation());
        verify(visitRepository, times(1)).existsById(1L);
        verify(visitRepository, times(1)).save(visit);
    }

    @Test
    void testGetVisitWithClinicalManifestations() {
        // Given
        when(visitRepository.findById(1L)).thenReturn(Optional.of(visit));

        // When
        Optional<Visit> result = visitService.getVisitById(1L);

        // Then
        assertTrue(result.isPresent());
        assertEquals("初诊临床表现：血压偏高，头晕头痛明显", result.get().getInitialVisitClinicalManifestation());
        assertEquals("复诊临床表现：症状有所缓解", result.get().getFollowUpClinicalManifestation());
        verify(visitRepository, times(1)).findById(1L);
    }

    @Test
    void testGetAllVisitsWithClinicalManifestations() {
        // Given
        List<Visit> visits = Arrays.asList(visit);
        when(visitRepository.findAll()).thenReturn(visits);

        // When
        List<Visit> result = visitService.getAllVisits();

        // Then
        assertEquals(1, result.size());
        assertEquals("初诊临床表现：血压偏高，头晕头痛明显", result.get(0).getInitialVisitClinicalManifestation());
        assertEquals("复诊临床表现：症状有所缓解", result.get(0).getFollowUpClinicalManifestation());
        verify(visitRepository, times(1)).findAll();
    }
}