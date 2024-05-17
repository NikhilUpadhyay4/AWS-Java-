package com.awsproject.repository;

import com.awsproject.model.JobStatus;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;


public interface JobStatusRepository extends MongoRepository<JobStatus, String> {
    Optional<JobStatus> findById(String Id);
}
