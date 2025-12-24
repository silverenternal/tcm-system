package com.tcm.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;

/**
 * 诊断图片实体类
 * 用于存储舌象图片等中医诊断图片
 */
@Entity
@Table(name = "diagnostic_images")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler", "visit"})
public class DiagnosticImage extends BaseEntity {
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "visit_id")
    @com.fasterxml.jackson.annotation.JsonIgnore
    private Visit visit;
    
    @Column(name = "image_type", length = 50)
    private String imageType; // 图片类型：tongue(舌象), face(面象), pulse(脉象)等
    
    @Column(name = "image_path", length = 500)
    private String imagePath; // 图片存储路径
    
    @Column(name = "image_name", length = 200)
    private String imageName; // 原始图片名称
    
    @Column(name = "image_size")
    private Long imageSize; // 图片大小（字节）
    
    @Column(name = "width")
    private Integer width; // 图片宽度
    
    @Column(name = "height")
    private Integer height; // 图片高度
    
    @Column(name = "original_format", length = 10)
    private String originalFormat; // 原始图片格式
    
    @Column(name = "processed_format", length = 10)
    private String processedFormat; // 处理后图片格式
    
    @Column(name = "description", length = 500)
    private String description; // 图片描述
    
    // 构造函数
    public DiagnosticImage() {}
    
    // Getters and Setters
    public Visit getVisit() {
        return visit;
    }
    
    public void setVisit(Visit visit) {
        this.visit = visit;
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
}