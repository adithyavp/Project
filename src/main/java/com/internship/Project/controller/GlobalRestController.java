package com.internship.Project.controller;

import com.internship.Project.entity.EmailDetails;
import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.service.GlobalRestService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@Slf4j
public class GlobalRestController {

    @Autowired
    GlobalRestService globalRestService;

    // This part is for CRUD operations of the Jobs master table
    //
    //
    @PostMapping("/jobs/create")
    public ResponseEntity<Jobs> createJob(@RequestBody Jobs jobDetails){
        log.info("Create new Job - Jobs Master Table");
        Jobs jobCreated = globalRestService.createJob(jobDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobCreated);
    }

    @GetMapping("/jobs/read")
    public ResponseEntity<List<Jobs>> readAllJobs(){
        log.info("Read all Jobs - Jobs Master Table");
        List<Jobs> readAllJobs = globalRestService.readAllJobs();
        return ResponseEntity.status(HttpStatus.OK).body(readAllJobs);
    }

    @PostMapping("/jobs/update/{jobId}/{jobName}")
    public ResponseEntity<Jobs> updateJob(@PathVariable Long jobId, @PathVariable String jobName, @RequestBody Jobs updateJobDetails){
        log.info("Update Job, Job ID: " + jobId + " Job Name: " + jobName + " - Jobs Master Table");
        Jobs updatedJob = globalRestService.updateJob(jobId, jobName, updateJobDetails);
        return ResponseEntity.status(HttpStatus.OK).body(updatedJob);
    }

    @PostMapping("/jobs/delete/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId){
        log.info("Delete Job, Job ID: " + jobId + " - Jobs Master Table");
        globalRestService.deleteJob(jobId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    // This part is for scheduling the jobs created in the Jobs master table
    //
    //
    @PostMapping("/jobs/scheduleAllGlobal")
    public ResponseEntity<Void> scheduleAllGlobalJobs(){
        log.info("Schedule all Global Jobs - Jobs Master Table");
        globalRestService.startGlobalJobScheduling();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

    @PostMapping("/jobs/scheduleAllLocal")
    public ResponseEntity<Void> scheduleAllLocalJobs(){
        log.info("Schedule all Local Jobs - Jobs Master Table");
        globalRestService.startLocalJobScheduling();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }


    // This part is for the Jobs Executed Table
    //
    //
    @GetMapping("/jobsExecuted/read")
    public ResponseEntity<List<JobsExecutedDetails>> readAllJobsExecuted(){
        log.info("Read all Jobs - Jobs Executed Details Table");
        List<JobsExecutedDetails> readAllJobsExecuted = globalRestService.readAllJobsExecuted();
        return ResponseEntity.status(HttpStatus.OK).body(readAllJobsExecuted);
    }


    // This part is for the CRUD operations of the EmailDetails table
    //
    //
    @PostMapping("/emailDetails/create")
    public ResponseEntity<EmailDetails> createEmailDetail(@RequestBody EmailDetails emailDetail){
        log.info("Create new Email Detail - Email Details Table");
        EmailDetails emailDetailCreated = globalRestService.createEmailDetail(emailDetail);
        return ResponseEntity.status(HttpStatus.CREATED).body(emailDetailCreated);
    }

    @GetMapping("/emailDetails/read")
    public ResponseEntity<List<EmailDetails>> readAllEmailDetails(){
        log.info("Read all Email Details - Email Details Table");
        List<EmailDetails> readAllEmailDetails = globalRestService.readAllEmailDetails();
        return ResponseEntity.status(HttpStatus.OK).body(readAllEmailDetails);
    }

    @PostMapping("/emailDetails/update/{srNo}/{emailId}")
    public ResponseEntity<EmailDetails> updateEmailDetail(@PathVariable Long srNo, @PathVariable String emailId, @RequestBody EmailDetails updateEmailDetail){
        log.info("Update Email Details, Serial No: " + srNo + " Email ID: " + emailId + " - Email Details Table");
        EmailDetails updatedEmailDetail = globalRestService.updateEmailDetail(srNo, emailId, updateEmailDetail);
        return ResponseEntity.status(HttpStatus.OK).body(updatedEmailDetail);
    }

    @PostMapping("/emailDetails/delete/{emailId}/{jobClassName}")
    public ResponseEntity<Void> deleteEmailDetail(@PathVariable String emailId, @PathVariable String jobClassName){
        log.info("Delete Email Detail, Email ID: " + emailId + " Job Class Name: " + jobClassName + "- Email Details Table");
        globalRestService.deleteEmailDetail(emailId, jobClassName);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }


    // Just for reference purpose
    // This end point is just to check the printing of the Trigger Key hashmap
    @GetMapping("/print")
    public ResponseEntity<Void> printHashMap(){
        globalRestService.printHashMap();
        return ResponseEntity.status(HttpStatus.OK).body(null);
    }

}