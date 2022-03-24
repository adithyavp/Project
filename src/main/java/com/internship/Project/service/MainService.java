package com.internship.Project.service;

import com.internship.Project.entity.Jobs;
import com.internship.Project.repository.JobsRepo;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

@Service
public class MainService {

    private static final Logger LOG = LoggerFactory.getLogger(MainService.class);

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    Scheduler scheduler;

    HashMap<Long,TriggerKey> mapOfJobIdAndTriggers = new HashMap<Long,TriggerKey>();

    public void scheduleAllGlobalJob() {

        LOG.info("Scheduling all Global jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "jdbc");
        Iterator<Jobs> iterator = list.iterator();

        if (!iterator.hasNext()){
            LOG.info("All the Global Jobs have been scheduled, Add new jobs for scheduling");
        }

        while(iterator.hasNext()){

            scheduleJobMethod(iterator.next());
        }

    }

    public void scheduleAllLocalJob() {


        LOG.info("Scheduling all Local jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "memory");
        Iterator<Jobs> iterator = list.iterator();

        if (!iterator.hasNext()){
            LOG.info("All the Local Jobs have been scheduled, Add new jobs for scheduling");
        }

        while (iterator.hasNext()) {

            scheduleJobMethod(iterator.next());
        }
    }

    private void scheduleJobMethod(Jobs job) {

        String jobName = job.getName();
        String jobMemory = job.getMemoryType();
        String jobCronExp = job.getCronExpression();
        String jobClass = job.getExecutionClass();

        JobDataMap dataMap = new JobDataMap();
        dataMap.put("jobId", job.getJobId());

        JobDetail jobDetail = null;
        try {
            jobDetail = JobBuilder
                    .newJob((Class<? extends QuartzJobBean>) Class.forName("com.internship.Project.scheduler."+jobClass))
                    .withIdentity(jobName, jobMemory)
                    .storeDurably(false)
                    .usingJobData(dataMap)
                    .build();

        } catch (ClassNotFoundException e) {
            LOG.error("Class not found during Scheduling Job", e);
        }

        Trigger jobTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity(jobName, jobMemory)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                .build();

        // Map to store the trigger keys, in this map
        // Key - Job Id, Value - trigger key
        mapOfJobIdAndTriggers.put(job.getJobId(), jobTrigger.getKey());

        try {
            scheduler.scheduleJob(jobDetail, jobTrigger);

            LOG.info("Job Scheduled Successfully");

            // Updating the status of the job from unscheduled to active
            job.setJobWorkingStatus("active");
            jobsRepo.save(job);
        } catch (SchedulerException e) {
            LOG.error("Exception while scheduling job: "+e);
        }

//            job.setJobWorkingStatus("active");
//            jobsRepo.save(job);
//
//            System.out.println(jobName);
//            System.out.println(jobMemory);
//            System.out.println(jobCronExp);
//            System.out.println(jobClass);
//            System.out.println(job.getJobWorkingStatus());

    }

    // This method passes the hashmap of Trigger Key which gets generated while creating triggers
    public HashMap passHashMapTriggerKey(){
        return mapOfJobIdAndTriggers;
    }

    // Just for reference purpose we are printing the Hashmap of the Trigger Key.
    public void printHashMap() {
        for (Map.Entry entry : mapOfJobIdAndTriggers.entrySet()) {
            System.out.println("Key: " + entry.getKey() + "  Value: " + entry.getValue());
        }
    }
}