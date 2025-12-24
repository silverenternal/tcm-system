package com.tcm.repository;

import com.tcm.model.MedicineInventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MedicineInventoryRepository extends JpaRepository<MedicineInventory, Long> {
    MedicineInventory findByMedicineCode(String medicineCode);
    MedicineInventory findByMedicineName(String medicineName);
}