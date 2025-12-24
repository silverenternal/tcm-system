package com.tcm.service;

import com.tcm.dto.DiagnosticImageResponse;
import com.tcm.model.DiagnosticImage;
import org.springframework.web.multipart.MultipartFile;
import java.util.List;

public interface DiagnosticImageService {
    DiagnosticImageResponse uploadDiagnosticImage(Long visitId, MultipartFile file, String imageType, String description) throws Exception;
    DiagnosticImageResponse getDiagnosticImageById(Long id);
    List<DiagnosticImageResponse> getDiagnosticImagesByVisitId(Long visitId);
    List<DiagnosticImageResponse> getDiagnosticImagesByImageType(String imageType);
    List<DiagnosticImageResponse> getAllDiagnosticImages();
    void deleteDiagnosticImage(Long id);
}