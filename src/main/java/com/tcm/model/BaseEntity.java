package com.tcm.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.tcm.utils.SnowflakeIdGenerator;
import jakarta.persistence.*;

import java.time.LocalDateTime;

/**
 * 基础实体类，包含所有实体的公共字段
 */
@MappedSuperclass
public abstract class BaseEntity {
    
    @Id
    @Column(name = "id", nullable = false, updatable = false)
    @JsonFormat(shape = JsonFormat.Shape.STRING)
    private Long id; // 使用雪花算法生成的分布式唯一ID
    
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 在创建实体时自动生成ID和创建时间
    @PrePersist
    protected void onCreate() {
        if (id == null) {
            // 使用雪花算法生成唯一ID
            SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(1, 1);
            this.id = idGenerator.nextId();
        }
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }
    
    // 在更新实体时更新更新时间
    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
    
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
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