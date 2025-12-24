package com.tcm.api;

import com.tcm.model.FollowUp;
import com.tcm.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

/**
 * 随访记录管理API控制器
 */
@RestController
@RequestMapping("/api/follow-ups")
public class FollowUpAPI {

    @Autowired
    private FollowUpService followUpService;

    /**
     * 获取所有随访记录
     */
    @GetMapping
    public ResponseEntity<List<FollowUp>> getAllFollowUps() {
        List<FollowUp> followUps = followUpService.getAllFollowUps();
        return ResponseEntity.ok(followUps);
    }

    /**
     * 根据ID获取随访记录
     */
    @GetMapping("/{id}")
    public ResponseEntity<FollowUp> getFollowUp(@PathVariable Long id) {
        Optional<FollowUp> followUp = followUpService.getFollowUpById(id);
        if (followUp.isPresent()) {
            return ResponseEntity.ok(followUp.get());
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 创建随访记录
     */
    @PostMapping
    public ResponseEntity<FollowUp> createFollowUp(@RequestBody FollowUp followUp) {
        FollowUp createdFollowUp = followUpService.createFollowUp(followUp);
        return ResponseEntity.status(HttpStatus.CREATED).body(createdFollowUp);
    }

    /**
     * 更新随访记录
     */
    @PutMapping("/{id}")
    public ResponseEntity<FollowUp> updateFollowUp(@PathVariable Long id, @RequestBody FollowUp followUp) {
        FollowUp updatedFollowUp = followUpService.updateFollowUp(id, followUp);
        if (updatedFollowUp != null) {
            return ResponseEntity.ok(updatedFollowUp);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    /**
     * 删除随访记录
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFollowUp(@PathVariable Long id) {
        followUpService.deleteFollowUp(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * 根据就诊ID获取随访记录
     */
    @GetMapping("/visit/{visitId}")
    public ResponseEntity<List<FollowUp>> getFollowUpsByVisitId(@PathVariable Long visitId) {
        List<FollowUp> followUps = followUpService.getFollowUpsByVisitId(visitId);
        return ResponseEntity.ok(followUps);
    }
}