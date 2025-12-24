package com.tcm.service.impl;

import com.tcm.model.FollowUp;
import com.tcm.repository.FollowUpRepository;
import com.tcm.service.FollowUpService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class FollowUpServiceImpl implements FollowUpService {
    
    @Autowired
    private FollowUpRepository followUpRepository;
    
    @Override
    public List<FollowUp> getAllFollowUps() {
        return followUpRepository.findAll();
    }
    
    @Override
    public Optional<FollowUp> getFollowUpById(Long id) {
        return followUpRepository.findById(id);
    }
    
    @Override
    public FollowUp createFollowUp(FollowUp followUp) {
        return followUpRepository.save(followUp);
    }
    
    @Override
    public FollowUp updateFollowUp(Long id, FollowUp followUp) {
        if (followUpRepository.existsById(id)) {
            followUp.setId(id);
            return followUpRepository.save(followUp);
        }
        return null; // 或抛出异常
    }
    
    @Override
    public void deleteFollowUp(Long id) {
        followUpRepository.deleteById(id);
    }
    
    @Override
    public List<FollowUp> getFollowUpsByVisitId(Long visitId) {
        return followUpRepository.findByVisitId(visitId);
    }
}