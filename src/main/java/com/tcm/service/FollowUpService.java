package com.tcm.service;

import com.tcm.model.FollowUp;
import java.util.List;
import java.util.Optional;

public interface FollowUpService {
    List<FollowUp> getAllFollowUps();
    Optional<FollowUp> getFollowUpById(Long id);
    FollowUp createFollowUp(FollowUp followUp);
    FollowUp updateFollowUp(Long id, FollowUp followUp);
    void deleteFollowUp(Long id);
    List<FollowUp> getFollowUpsByVisitId(Long visitId);
}