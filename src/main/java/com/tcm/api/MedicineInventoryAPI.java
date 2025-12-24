package com.tcm.api;

import com.tcm.model.MedicineInventory;
import com.tcm.service.MedicineInventoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 药品库存管理API控制器
 */
@RestController
@RequestMapping("/api/medicine-inventory")
public class MedicineInventoryAPI {

    @Autowired
    private MedicineInventoryService medicineInventoryService;

    /**
     * 获取所有药品库存
     */
    @GetMapping
    public ResponseEntity<List<MedicineInventory>> getAllMedicineInventory() {
        List<MedicineInventory> medicineInventory = medicineInventoryService.getAllMedicineInventory();
        return ResponseEntity.ok(medicineInventory);
    }

    /**
     * 根据ID获取药品库存
     */
    @GetMapping("/{id}")
    public ResponseEntity<MedicineInventory> getMedicineInventory(@PathVariable Long id) {
        Optional<MedicineInventory> medicineInventory = medicineInventoryService.getMedicineInventoryById(id);
        if (medicineInventory.isPresent()) {
            return ResponseEntity.ok(medicineInventory.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建药品库存
     */
    @PostMapping
    public ResponseEntity<MedicineInventory> createMedicineInventory(@RequestBody MedicineInventory medicineInventory) {
        MedicineInventory createdMedicineInventory = medicineInventoryService.createMedicineInventory(medicineInventory);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdMedicineInventory);
    }

    /**
     * 更新药品库存
     */
    @PutMapping("/{id}")
    public ResponseEntity<MedicineInventory> updateMedicineInventory(@PathVariable Long id, @RequestBody MedicineInventory medicineInventory) {
        MedicineInventory updatedMedicineInventory = medicineInventoryService.updateMedicineInventory(id, medicineInventory);
        if (updatedMedicineInventory != null) {
            return ResponseEntity.ok(updatedMedicineInventory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除药品库存
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteMedicineInventory(@PathVariable Long id) {
        medicineInventoryService.deleteMedicineInventory(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据药品编码查找药品
     */
    @GetMapping("/code/{medicineCode}")
    public ResponseEntity<MedicineInventory> getMedicineByCode(@PathVariable String medicineCode) {
        MedicineInventory medicineInventory = medicineInventoryService.findByMedicineCode(medicineCode);
        if (medicineInventory != null) {
            return ResponseEntity.ok(medicineInventory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
    
    /**
     * 根据药品名称查找药品
     */
    @GetMapping("/name/{medicineName}")
    public ResponseEntity<MedicineInventory> getMedicineByName(@PathVariable String medicineName) {
        MedicineInventory medicineInventory = medicineInventoryService.findByMedicineName(medicineName);
        if (medicineInventory != null) {
            return ResponseEntity.ok(medicineInventory);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}