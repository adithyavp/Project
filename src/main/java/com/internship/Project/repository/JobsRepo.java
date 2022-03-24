package com.internship.Project.repository;

import com.internship.Project.entity.Jobs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobsRepo extends CrudRepository<Jobs, Long> {

    Jobs findByJobIdAndName(Long jobId, String name);

    Jobs findByName(String name);

    List<Jobs> findByJobWorkingStatusAndAndMemoryType(String jobWorkingStatus, String memoryType);

    Jobs findByJobId(Long jobId);

    List<Jobs> findByJobWorkingStatus(String jobWorkingStatus);
}