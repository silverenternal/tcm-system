package com.tcm.dto;

import java.time.LocalDateTime;

/**
 * 诊断图片响应DTO
 * 用于避免序列化JPA实体时的循环引用问题
 */
public class DiagnosticImageResponse {
    
    private Long id;
    private Long visitId;  // 只返回ID，而不是整个实体
    private String imageType;
    private String imagePath;
    private String imageName;
    private Long imageSize;
    private Integer width;
    private Integer height;
    private String originalFormat;
    private String processedFormat;
    private String description;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    
    // 构造函数
    public DiagnosticImageResponse() {}
    
    public DiagnosticImageResponse(Long id, Long visitId, String imageType, String imagePath, 
                                   String imageName, Long imageSize, Integer width, Integer height, 
                                   String originalFormat, String processedFormat, String description,
                                   LocalDateTime createdAt, LocalDateTime updatedAt) {
        this.id = id;
        this.visitId = visitId;
        this.imageType = imageType;
        this.imagePath = imagePath;
        this.imageName = imageName;
        this.imageSize = imageSize;
        this.width = width;
        this.height = height;
        this.originalFormat = originalFormat;
        this.processedFormat = processedFormat;
        this.description = description;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public Long getVisitId() {
        return visitId;
    }
    
    public void setVisitId(Long visitId) {
        this.visitId = visitId;
    }
    
    public String getImageType() {
        return imageType;
    }
    
    public void setImageType(String imageType) {
        this.imageType = imageType;
    }
    
    public String getImagePath() {
        return imagePath;
    }
    
    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
    
    public String getImageName() {
        return imageName;
    }
    
    public void setImageName(String imageName) {
        this.imageName = imageName;
    }
    
    public Long getImageSize() {
        return imageSize;
    }
    
    public void setImageSize(Long imageSize) {
        this.imageSize = imageSize;
    }
    
    public Integer getWidth() {
        return width;
    }
    
    public void setWidth(Integer width) {
        this.width = width;
    }
    
    public Integer getHeight() {
        return height;
    }
    
    public void setHeight(Integer height) {
        this.height = height;
    }
    
    public String getOriginalFormat() {
        return originalFormat;
    }
    
    public void setOriginalFormat(String originalFormat) {
        this.originalFormat = originalFormat;
    }
    
    public String getProcessedFormat() {
        return processedFormat;
    }
    
    public void setProcessedFormat(String processedFormat) {
        this.processedFormat = processedFormat;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
}