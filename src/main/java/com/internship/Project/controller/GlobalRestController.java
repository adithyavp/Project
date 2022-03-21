package com.internship.Project.controller;

import com.internship.Project.entity.Jobs;
import com.internship.Project.service.GlobalRestService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class GlobalRestController {

    @Autowired
    GlobalRestService globalRestService;

    @PostMapping("/createJob")
    public ResponseEntity<Jobs> createJob(@RequestBody Jobs jobDetails){
        Jobs jobCreated = globalRestService.createJob(jobDetails);
        return ResponseEntity.status(HttpStatus.CREATED).body(jobCreated);
    }

    @GetMapping("/viewJobs")
    public ResponseEntity<List<Jobs>> readAllJobs(){
        List<Jobs> readAllJobs = globalRestService.readAllJobs();
        return ResponseEntity.status(HttpStatus.OK).body(readAllJobs);
    }

    @PostMapping("/updateJob/{jobId}/{jobName}")
    public ResponseEntity<Jobs> updateJob(@PathVariable Long jobId, @PathVariable String jobName, @RequestBody Jobs updateJobDetails){
        Jobs updatedJob = globalRestService.updateJob(jobId, jobName, updateJobDetails);
        return ResponseEntity.status(HttpStatus.OK).body(updatedJob);
    }

    @PostMapping("/deleteJob/{jobId}")
    public ResponseEntity<Void> deleteJob(@PathVariable Long jobId){
        globalRestService.deleteJob(jobId);
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(null);
    }

}
