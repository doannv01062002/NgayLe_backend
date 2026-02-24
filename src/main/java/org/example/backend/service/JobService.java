package org.example.backend.service;

import org.example.backend.dto.JobDTO;
import org.example.backend.model.entity.Job;
import org.example.backend.model.entity.User;
import org.example.backend.repository.JobRepository;
import org.example.backend.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JobService {
    @Autowired
    private JobRepository jobRepository;
    @Autowired
    private UserRepository userRepository;

    public List<JobDTO> getAllJobs() {
        return jobRepository.findAll().stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public List<JobDTO> getJobsByHirer(Long hirerId) {
        User hirer = userRepository.findById(hirerId).orElseThrow(() -> new RuntimeException("User not found"));
        return jobRepository.findByHirer(hirer).stream().map(this::mapToDTO).collect(Collectors.toList());
    }

    public JobDTO getJobById(Long jobId) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        return mapToDTO(job);
    }

    @Transactional
    public JobDTO createJob(Long hirerId, JobDTO dto) {
        User hirer = userRepository.findById(hirerId).orElseThrow(() -> new RuntimeException("User not found"));
        Job job = Job.builder()
                .hirer(hirer)
                .title(dto.getTitle())
                .description(dto.getDescription())
                .location(dto.getLocation())
                .budget(dto.getBudget())
                .deadline(dto.getDeadline())
                .status(Job.Status.OPEN)
                .build();

        job = jobRepository.save(job);
        return mapToDTO(job);
    }

    @Transactional
    public JobDTO updateJob(Long jobId, JobDTO dto) {
        Job job = jobRepository.findById(jobId).orElseThrow(() -> new RuntimeException("Job not found"));
        if (dto.getTitle() != null)
            job.setTitle(dto.getTitle());
        if (dto.getDescription() != null)
            job.setDescription(dto.getDescription());
        if (dto.getLocation() != null)
            job.setLocation(dto.getLocation());
        if (dto.getBudget() != null)
            job.setBudget(dto.getBudget());
        if (dto.getDeadline() != null)
            job.setDeadline(dto.getDeadline());
        if (dto.getStatus() != null)
            job.setStatus(dto.getStatus());

        job = jobRepository.save(job);
        return mapToDTO(job);
    }

    @Transactional
    public void deleteJob(Long jobId) {
        jobRepository.deleteById(jobId);
    }

    private JobDTO mapToDTO(Job job) {
        JobDTO dto = new JobDTO();
        dto.setJobId(job.getJobId());
        dto.setHirerId(job.getHirer().getUserId());
        dto.setHirerName(job.getHirer().getFullName());
        dto.setTitle(job.getTitle());
        dto.setDescription(job.getDescription());
        dto.setLocation(job.getLocation());
        dto.setBudget(job.getBudget());
        dto.setDeadline(job.getDeadline());
        dto.setStatus(job.getStatus());
        dto.setCreatedAt(job.getCreatedAt());
        return dto;
    }
}
