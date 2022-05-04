package com.internship.project.service;

import com.internship.project.entity.Jobs;
import com.internship.project.repository.CurrencyDataRepo;
import com.internship.project.repository.JobExecutedDetailsRepo;
import com.internship.project.repository.JobsRepo;

import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/* This is the Global Scheduler Service class which has all the required method executions for the GlobalRestService and other
   functionalities required */
@Service
@Slf4j
public class GlobalSchedulerService {

    @Autowired
    Scheduler globalScheduler;

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    CurrencyDataRepo currencyDataRepo;

    HashMap<Long, TriggerKey> mapOfJobIdAndTriggers = new HashMap<Long, TriggerKey>();

    public void start() {
        try {

            globalScheduler.start();

            log.info("Quartz Global Scheduler has started");

            scheduleAllGlobalJob();

        } catch (SchedulerException e) {
            log.error("Exception while initializing/starting scheduler: ", e);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public void stop() {
        try {
            Iterable<Jobs> iterable = jobsRepo.findByJobWorkingStatus("active");
            for (Jobs job : iterable) {
                job.setJobWorkingStatus("unscheduled");

                jobsRepo.save(job);

//                JobKey jobKey = new JobKey(job.getName(), job.getMemoryType());
//
//                if (job.getMemoryType().contains("jdbc")) {
//                    globalScheduler.deleteJob(jobKey);
//                }
            }

            globalScheduler.shutdown();

            log.info("Quartz Scheduler shutdown");
			log.info("The active jobs status updated to unscheduled, the jobs related to it have been deleted - Jobs Master Table, Quartz Job Tables(qrtz_triggers, qrtz_cron_triggers, qrtz_job_details)");

        } catch (SchedulerException e) {
            log.error("Exception - Shutting down Quartz scheduler: ");
        }
    }

    public void scheduleAllGlobalJob() {

        log.info("Scheduling all Global jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "jdbc");
        Iterator<Jobs> iterator = list.iterator();

        if (!iterator.hasNext()) {
            log.info("Add new jobs for scheduling");
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
        dataMap.put("count", 0);
        dataMap.put("jobName", jobName);

        JobDetail jobDetail = null;

        try {
            jobDetail = JobBuilder
                    .newJob(Class.forName("com.internship.project.scheduler." + jobClass).asSubclass(Job.class))
                    .withIdentity(jobName, jobMemory)
                    .storeDurably(false)
                    .usingJobData(dataMap)
                    .build();

        } catch (ClassNotFoundException e) {
            log.error("Class not found during Scheduling Job", e);
        }


        Trigger jobTrigger = TriggerBuilder
                .newTrigger()
                .withIdentity(jobName, jobMemory)
                .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                .build();

        /* Map to store the trigger keys, in this map
         Key - Job Id, Value - trigger key */
        mapOfJobIdAndTriggers.put(job.getJobId(), jobTrigger.getKey());

        try {
            if (jobClass.contains("Global")) {
                globalScheduler.scheduleJob(jobDetail, jobTrigger);
            }

            log.info("Job Scheduled Successfully");

            // Updating the status of the job from unscheduled to active
            job.setJobWorkingStatus("active");
            jobsRepo.save(job);
        } catch (SchedulerException e) {
            log.error("Exception while scheduling job: " + e);
        }
    }
}