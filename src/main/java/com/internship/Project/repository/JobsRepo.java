package com.internship.Project.repository;

import com.internship.Project.entity.Jobs;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

import java.util.List;

public interface JobsRepo extends CrudRepository<Jobs, Long> {

    public Jobs findByJobIdAndAndName(Long jobId, String name);

    public Jobs findByName(String name);

    public List<Jobs> findByJobWorkingStatusAndAndMemoryType(String jobWorkingStatus, String memoryType);

    public Jobs findByJobId(Long jobId);
}