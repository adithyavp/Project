package com.internship.project.repository;

import com.internship.project.entity.Jobs;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobsRepo extends CrudRepository<Jobs, Long> {

    Jobs findByJobIdAndName(Long jobId, String name);

    Jobs findByName(String name);

    List<Jobs> findByJobWorkingStatusAndAndMemoryType(String jobWorkingStatus, String memoryType);

    Jobs findByJobId(Long jobId);

    List<Jobs> findByJobWorkingStatus(String jobWorkingStatus);

}