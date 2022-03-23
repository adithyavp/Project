package com.internship.Project.service;

import com.internship.Project.entity.Jobs;
import com.internship.Project.repository.JobsRepo;
import com.internship.Project.scheduler.GlobalJob1;
import com.internship.Project.scheduler.LocalJob1;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Iterator;

@Service
public class MainService {

    private static final Logger LOG = LoggerFactory.getLogger(MainService.class);

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    Scheduler scheduler;

    public void scheduleGlobalJob(){

        LOG.info("Scheduling all Global jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "jdbc");
        Iterator<Jobs> iterator = list.iterator();

        if (!iterator.hasNext()){
            LOG.info("All the Global Jobs have been scheduled, Add new jobs for scheduling");
        }

        while(iterator.hasNext()){
            Jobs job = iterator.next();
            String jobName = job.getName();
            String jobMemory = job.getMemoryType();
            String jobCronExp = job.getCronExpression();
            String jobClass = job.getExecutionClass();

            JobDataMap dataMap = new JobDataMap();
            dataMap.put("jobId", job.getJobId());

            JobDetail globalJobDetail = JobBuilder
                    .newJob(GlobalJob1.class)
                    .withIdentity(jobName, jobMemory)
                    .storeDurably(false)
                    .usingJobData(dataMap)
                    .build();

            Trigger globalJobTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(jobName, jobMemory)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                    .build();



            try {
                scheduler.scheduleJob(globalJobDetail, globalJobTrigger);

                LOG.info("Global job Scheduled Successfully");

                // Updating the status of the job from unscheduled to active
                job.setJobWorkingStatus("active");
                jobsRepo.save(job);
            } catch (SchedulerException e) {
                LOG.error("Exception while scheduling a global job: "+e);
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

    }


    public void scheduleLocalJob() {


        LOG.info("Scheduling all Local jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "memory");
        Iterator<Jobs> iterator = list.iterator();

        if (!iterator.hasNext()){
            LOG.info("All the Local Jobs have been scheduled, Add new jobs for scheduling");
        }

        while (iterator.hasNext()) {
            Jobs job = iterator.next();
            String jobName = job.getName();
            String jobMemory = job.getMemoryType();
            String jobCronExp = job.getCronExpression();
            String jobClass = job.getExecutionClass();

            JobDataMap dataMap = new JobDataMap();
            dataMap.put("jobId", job.getJobId());

            JobDetail localJobDetail = JobBuilder
                    .newJob(LocalJob1.class)
                    .withIdentity(jobName, jobMemory)
                    .storeDurably(false)
                    .usingJobData(dataMap)
                    .build();

            Trigger localJobTrigger = TriggerBuilder
                    .newTrigger()
                    .withIdentity(jobName, jobMemory)
                    .withSchedule(CronScheduleBuilder.cronSchedule(jobCronExp))
                    .build();


            try {
                scheduler.scheduleJob(localJobDetail, localJobTrigger);

                LOG.info("Local job Scheduled Successfully");

                // Updating the status of the job from unscheduled to active
                job.setJobWorkingStatus("active");
                jobsRepo.save(job);
            } catch (SchedulerException e) {
                LOG.error("Exception while scheduling a local job: " + e);
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
    }
}
