package com.internship.Project.service;


import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.JobExecutedDetailsRepo;
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

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    MainService mainService;

    // For the Jobs master table
    //
    //
    //

    // Creation of the job details in the jobs table
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

    // To read all the jobs from the jobs table
    public List<Jobs> readAllJobs(){
        Iterable<Jobs> allJobs = jobsRepo.findAll();
        return (List<Jobs>) allJobs;
    }

    // To update the jobs in the Jobs table
    // One can update only the Memory type and the Cron expression and no other data
    public Jobs updateJob(Long jobId, String jobName, Jobs job){
        Jobs isJobPresent = jobsRepo.findByJobIdAndAndName(jobId, jobName);
        if(isJobPresent!=null){
            isJobPresent.setMemoryType(job.getMemoryType());
            isJobPresent.setJobClass(job.getJobClass());
            isJobPresent.setCronExpression(job.getCronExpression());

            jobsRepo.save(isJobPresent);
        } else{
            System.out.println("Job Not Found");
        }
        return isJobPresent;
    }

    // To delete the jobs from the jobs table
    public void deleteJob(Long jobId){
        Optional<Jobs> jobToDelete = jobsRepo.findById(jobId);
        if(jobToDelete.isPresent()){
            jobsRepo.delete(jobToDelete.get());
        } else{
            System.out.println("Job not present");
        }
    }

    // Scheduling all Global jobs created
    public void startGlobalJobScheduling(){
        mainService.scheduleGlobalJob();
    }

    // Scheduling all Local jobs created
    public void startLocalJobScheduling(){
        mainService.scheduleLocalJob();
    }



    // For the Jobs Executed Details table
    //
    //
    //

    // To read all the jobs from the jobs table
    public List<JobsExecutedDetails> readAllJobsExecuted(){
        Iterable<JobsExecutedDetails> allExecutedJobs = jobExecutedDetailsRepo.findAll();
        return (List<JobsExecutedDetails>) allExecutedJobs;
    }

}