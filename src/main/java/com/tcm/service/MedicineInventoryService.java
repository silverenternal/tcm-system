package com.tcm.service;

import com.tcm.model.MedicineInventory;
import java.util.List;
import java.util.Optional;

public interface MedicineInventoryService {
    List<MedicineInventory> getAllMedicineInventory();
    Optional<MedicineInventory> getMedicineInventoryById(Long id);
    MedicineInventory createMedicineInventory(MedicineInventory medicineInventory);
    MedicineInventory updateMedicineInventory(Long id, MedicineInventory medicineInventory);
    void deleteMedicineInventory(Long id);
    MedicineInventory findByMedicineCode(String medicineCode);
    MedicineInventory findByMedicineName(String medicineName);
}