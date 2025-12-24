package com.tcm.repository;

import com.tcm.model.DiagnosticImage;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface DiagnosticImageRepository extends JpaRepository<DiagnosticImage, Long> {
    List<DiagnosticImage> findByVisitId(Long visitId);
    List<DiagnosticImage> findByImageType(String imageType);
    List<DiagnosticImage> findByVisitIdAndImageType(Long visitId, String imageType);
}