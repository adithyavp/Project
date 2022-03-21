package com.internship.Project.service;


import com.internship.Project.entity.Jobs;
import com.internship.Project.repository.JobsRepo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GlobalRestService {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalRestService.class);

    @Autowired
    JobsRepo jobsRepo;

    public Jobs createJob(Jobs job){
        Jobs newJobToCreate = jobsRepo.findByName(job.getName());
        if(newJobToCreate!=null){
            System.out.println("Job is already created");
        }
        else{
            job.setJobWorkingStatus("unscheduled");
            newJobToCreate = jobsRepo.save(job);
        }

        return  newJobToCreate;
    }

    public List<Jobs> readAllJobs(){
        Iterable<Jobs> allJobs = jobsRepo.findAll();
        return (List<Jobs>) allJobs;
    }

    // One can update only the Memory type and the Cron expression and no other data
    public Jobs updateJob(Long jobId, String jobName, Jobs job){
        Jobs isJobPresent = jobsRepo.findByJobIdAndAndName(jobId, jobName);
        if(isJobPresent!=null){
            isJobPresent.setMemoryType(job.getMemoryType());
            isJobPresent.setCronExpression(job.getCronExpression());

            jobsRepo.save(isJobPresent);
        } else{
            System.out.println("Job Not Found");
        }
        return isJobPresent;
    }

    public void deleteJob(Long jobId){
        Optional<Jobs> jobToDelete = jobsRepo.findById(jobId);
        if(jobToDelete.isPresent()){
            jobsRepo.delete(jobToDelete.get());
        } else{
            System.out.println("Job not present");
        }
    }


}