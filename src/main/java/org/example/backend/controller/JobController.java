package org.example.backend.controller;

import org.example.backend.dto.JobDTO;
import org.example.backend.service.JobService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/jobs")
public class JobController {
    @Autowired
    private JobService jobService;

    @GetMapping
    public ResponseEntity<List<JobDTO>> getAllJobs() {
        return ResponseEntity.ok(jobService.getAllJobs());
    }

    @GetMapping("/hirer/{hirerId}")
    public ResponseEntity<List<JobDTO>> getJobsByHirer(@PathVariable Long hirerId) {
        return ResponseEntity.ok(jobService.getJobsByHirer(hirerId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobDTO> getJobById(@PathVariable Long id) {
        return ResponseEntity.ok(jobService.getJobById(id));
    }

    @PostMapping("/hirer/{hirerId}")
    public ResponseEntity<JobDTO> createJob(@PathVariable Long hirerId, @RequestBody JobDTO dto) {
        return ResponseEntity.ok(jobService.createJob(hirerId, dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<JobDTO> updateJob(@PathVariable Long id, @RequestBody JobDTO dto) {
        return ResponseEntity.ok(jobService.updateJob(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long id) {
        jobService.deleteJob(id);
        return ResponseEntity.ok().build();
    }
}
