package com.tcm.service.impl;

import com.tcm.model.MedicineInventory;
import com.tcm.repository.MedicineInventoryRepository;
import com.tcm.service.MedicineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MedicineInventoryServiceImpl implements MedicineInventoryService {
    
    @Autowired
    private MedicineInventoryRepository medicineInventoryRepository;
    
    @Override
    public List<MedicineInventory> getAllMedicineInventory() {
        return medicineInventoryRepository.findAll();
    }
    
    @Override
    public Optional<MedicineInventory> getMedicineInventoryById(Long id) {
        return medicineInventoryRepository.findById(id);
    }
    
    @Override
    public MedicineInventory createMedicineInventory(MedicineInventory medicineInventory) {
        return medicineInventoryRepository.save(medicineInventory);
    }
    
    @Override
    public MedicineInventory updateMedicineInventory(Long id, MedicineInventory medicineInventory) {
        if (medicineInventoryRepository.existsById(id)) {
            medicineInventory.setId(id);
            return medicineInventoryRepository.save(medicineInventory);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deleteMedicineInventory(Long id) {
        medicineInventoryRepository.deleteById(id);
    }
    
    @Override
    public MedicineInventory findByMedicineCode(String medicineCode) {
        return medicineInventoryRepository.findByMedicineCode(medicineCode);
    }
    
    @Override
    public MedicineInventory findByMedicineName(String medicineName) {
        return medicineInventoryRepository.findByMedicineName(medicineName);
    }
}