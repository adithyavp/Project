package com.internship.Project.repository;

import com.internship.Project.entity.JobsExecutedDetails;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JobExecutedDetailsRepo extends CrudRepository<JobsExecutedDetails, Long> {

    JobsExecutedDetails findTopByOrderByJobsExecutedIdDesc();

}
