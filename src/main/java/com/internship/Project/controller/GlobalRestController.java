package com.internship.Project.controller;

import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.service.GlobalRestService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GlobalRestController {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalRestController.class);

    @Autowired
    GlobalRestService globalRestService;

    // This part is for CRUD operations of the Jobs master table
    //
    //
    @PostMapping("/jobs/create")
    public ResponseEntity<Jobs> createJob(@RequestBody Jobs jobDetails){
        Jobs jobCreated = globalRestService.createJob(jobDetails);
        LOG.info("Create new Job - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.CREATED).body(jobCreated);
    }

    @GetMapping("/jobs/read")
    public ResponseEntity<List<Jobs>> readAllJobs(){
        List<Jobs> readAllJobs = globalRestService.readAllJobs();
        LOG.info("Read all Jobs - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.OK).body(readAllJobs);
    }

    @PostMapping("/jobs/update/{jobId}/{jobName}")
    public ResponseEntity<Jobs> updateJob(@PathVariable Long jobId, @PathVariable String jobName, @RequestBody Jobs updateJobDetails){
        Jobs updatedJob = globalRestService.updateJob(jobId, jobName, updateJobDetails);
        LOG.info("Update Job, Job ID: " + jobId + " Job Name: " + jobName + " - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.OK).body(updatedJob);
    }

    @PostMapping("/jobs/delete/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId){
        globalRestService.deleteJob(jobId);
        LOG.info("Delete Job, Job ID: " + jobId + " - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    // This part is for scheduling the jobs created in the Jobs master table
    //
    //
    @PostMapping("/jobs/scheduleGlobal")
    public ResponseEntity<Void> scheduleGlobalJobs(){
        globalRestService.startGlobalJobScheduling();
        LOG.info("Schedule all Global Jobs - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/jobs/scheduleLocal")
    public ResponseEntity<Void> scheduleLocalJobs(){
        globalRestService.startLocalJobScheduling();
        LOG.info("Schedule all Local Jobs - Jobs Master Table");
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    // This part is for the Jobs Executed Table
    //
    //
    @GetMapping("/jobsExecuted/read")
    public ResponseEntity<List<JobsExecutedDetails>> readAllJobsExecuted(){
        List<JobsExecutedDetails> readAllJobsExecuted = globalRestService.readAllJobsExecuted();
        LOG.info("Read all Jobs - Jobs Executed Details Table");
        return ResponseEntity.status(HttpStatus.OK).body(readAllJobsExecuted);
    }

}
