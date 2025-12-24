package com.tcm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.SQLException;

@Configuration
public class DatabaseConfig {

    @Autowired
    private DataSource dataSource;

    @EventListener(ApplicationReadyEvent.class)
    public void updateDatabaseSchema() {
        try (Connection connection = dataSource.getConnection();
             Statement statement = connection.createStatement()) {
            
            // 尝试修改id_card列以支持更长的值
            // 首先检查当前列定义
            System.out.println("Attempting to update id_card column length...");
            
            // 尝试修改列长度
            String alterTableSQL = "ALTER TABLE patients ALTER COLUMN id_card TYPE VARCHAR(50)";
            try {
                statement.executeUpdate(alterTableSQL);
                System.out.println("Successfully updated id_card column length to VARCHAR(50)");
            } catch (SQLException e) {
                System.out.println("Could not update id_card column length (may already be correct): " + e.getMessage());
            }
            
            // 尝试移除可能存在的长度限制或设置默认值
            String modifyDefaultSQL = "ALTER TABLE patients ALTER COLUMN id_card DROP DEFAULT";
            try {
                statement.executeUpdate(modifyDefaultSQL);
                System.out.println("Successfully dropped id_card default value");
            } catch (SQLException e) {
                System.out.println("Could not modify id_card default (may not exist): " + e.getMessage());
            }
            
        } catch (SQLException e) {
            System.out.println("Database connection error: " + e.getMessage());
        }
    }
}