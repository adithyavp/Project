package com.internship.Project.repository;

import com.internship.Project.entity.JobsExecutedDetails;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;

public interface JobExecutedDetailsRepo extends CrudRepository<JobsExecutedDetails, Long> {

    JobsExecutedDetails findTopByOrderByJobsExecutedIdDesc();

}
