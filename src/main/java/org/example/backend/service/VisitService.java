package org.example.backend.service;

import org.example.backend.model.entity.VisitLog;
import org.example.backend.repository.VisitLogRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@Transactional
public class VisitService {

    @Autowired
    private VisitLogRepository visitLogRepository;

    @SuppressWarnings("null")
    public void trackVisit() {
        VisitLog log = VisitLog.builder()
                .visitTime(LocalDateTime.now())
                .build();
        visitLogRepository.save(log);
    }
}
