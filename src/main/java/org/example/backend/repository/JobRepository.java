package org.example.backend.repository;

import org.example.backend.model.entity.Job;
import org.example.backend.model.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long> {
    List<Job> findByHirer(User hirer);

    List<Job> findByStatus(Job.Status status);
}
