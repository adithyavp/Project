package com.internship.Project.service;

import com.internship.Project.configuration.AutowiringSpringBeanJobFactory;
import com.internship.Project.entity.Jobs;
import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.EmailDetailsRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;

import lombok.extern.slf4j.Slf4j;
import org.quartz.*;

import org.quartz.spi.JobFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;

/* This is the main service class which has all the required method executions for the GlobalRestService and other
   functionalities required */
@Service
@Slf4j
public class MainService {

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    CurrencyDataRepo currencyDataRepo;

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    PlatformTransactionManager transactionManager;

    Scheduler globalScheduler;

    Scheduler localScheduler;

    HashMap<Long, TriggerKey> mapOfJobIdAndTriggers = new HashMap<Long, TriggerKey>();

//    public MainService(JobsRepo jobsRepo) {
//        this.jobsRepo = jobsRepo;
//    }
//
//    public JobsRepo getJobsRepo() {
//        return jobsRepo;
//    }
//
//    public void setJobsRepo(JobsRepo jobsRepo) {
//        this.jobsRepo = jobsRepo;
//    }

    public void start() {
        try {

////            scheduler.start();
//
//            SchedulerFactoryBean factory = new SchedulerFactoryBean();
//
//            factory.setOverwriteExistingJobs(true);
//
//            AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
//
//            jobFactory.setApplicationContext(applicationContext);
//
//            factory.setJobFactory(jobFactory);
//
//            Properties overAllPropertiesGlobal = readQuartzProperties("globalQuartz.properties");
//
//            factory.setQuartzProperties(overAllPropertiesGlobal);
//
//            factory.afterPropertiesSet();

            globalScheduler = (Scheduler) applicationContext.getBean("getGlobalJobScheduler");


//            globalScheduler.getContext().put("jobRepository", jobsRepo);
//
//            globalScheduler.getContext().put("jobExecutedDetailsRepository", jobExecutedDetailsRepo);
//
//            globalScheduler.getContext().put("currencyDataRepository", currencyDataRepo);

            // This part is for the initialization of the scheduler instance for the Global Jobs
//            StdSchedulerFactory stdSchedulerFactoryGlobal = new StdSchedulerFactory("globalQuartz.properties");

//            Properties overAllPropertiesGlobal = readQuartzProperties("globalQuartz.properties");
//
//            stdSchedulerFactoryGlobal.initialize(overAllPropertiesGlobal);

//            globalScheduler = stdSchedulerFactoryGlobal.getScheduler();

//            globalScheduler.start();

//            globalScheduler.getContext().put("jobRepository", jobsRepo);

            log.info("Quartz Global Scheduler has started");

//            // This part is for the initialization of the scheduler instance for the Local Jobs
//            StdSchedulerFactory stdSchedulerFactoryLocal = new StdSchedulerFactory("localQuartz.properties");
//
////            Properties overAllPropertiesLocal = readQuartzProperties("localQuartz.properties");
////
////            stdSchedulerFactoryLocal.initialize(overAllPropertiesLocal);
//
//            localScheduler = stdSchedulerFactoryLocal.getScheduler();

            localScheduler = (Scheduler) applicationContext.getBean("getLocalJobScheduler");

            localScheduler.start();

            log.info("Quartz Local Scheduler has started");

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

                JobKey jobKey = new JobKey(job.getName(), job.getMemoryType());

                if (job.getMemoryType().contains("jdbc")) {
                    globalScheduler.deleteJob(jobKey);
                    globalScheduler.clear();
                }
                if (job.getMemoryType().contains("memory")) {
                    localScheduler.deleteJob(jobKey);
                }

//                scheduler.deleteJob(jobKey);

            }

            globalScheduler.shutdown();

            localScheduler.shutdown();

//            scheduler.shutdown();

            log.info("Quartz Scheduler shutdown");
			log.info("The active jobs status updated to unscheduled, the jobs related to it have been deleted - Jobs Master Table, Quartz Job Tables(qrtz_triggers, qrtz_cron_triggers, qrtz_job_details)");

        } catch (SchedulerException e) {
            log.error("Exception - Shutting down Quartz scheduler: ");
        }
    }


    public JobFactory jobFactory(ApplicationContext applicationContext, JobsRepo jobsRepo, JobExecutedDetailsRepo jobExecutedDetailsRepo, CurrencyDataRepo currencyDataRepo, EmailDetailsRepo emailDetailsRepo){
        AutowiringSpringBeanJobFactory jobFactory = new AutowiringSpringBeanJobFactory();
        jobFactory.setApplicationContext(applicationContext);
        return jobFactory;
    }

    private Properties readQuartzProperties(String propertiesFileName) {

        Properties properties = new Properties();

        try {
            FileReader reader = new FileReader("E:\\Internship\\Project\\src\\main\\resources\\" + propertiesFileName);

            properties.load(reader);
        } catch (FileNotFoundException e) {
            log.error("Exception - File not found ", e);
        } catch (IOException e) {
            log.error("Exception while reading file: ", e);
        }

        return properties;
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

    public void scheduleAllLocalJob() {


        log.info("Scheduling all Local jobs from Jobs master table");

        Iterable<Jobs> list = jobsRepo.findByJobWorkingStatusAndAndMemoryType("unscheduled", "memory");
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
//        dataMap.put("Configurations", new Configurations(jobsRepo, jobExecutedDetailsRepo, currencyDataRepo));


        // This line of code is to check the constructor
//        GlobalJob1 globalJob1 = new GlobalJob1(jobsRepo);

        JobDetail jobDetail = null;

        try {
            jobDetail = JobBuilder
                    .newJob(Class.forName("com.internship.Project.scheduler." + jobClass).asSubclass(Job.class))
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

        // Map to store the trigger keys, in this map
        // Key - Job Id, Value - trigger key
        mapOfJobIdAndTriggers.put(job.getJobId(), jobTrigger.getKey());

//        AutowiringSpringBeanJobFactory customJobFactory = new AutowiringSpringBeanJobFactory();
//
//        customJobFactory.setApplicationContext(applicationContext);

        try {
            if (jobClass.contains("Global")) {
//                globalScheduler.setJobFactory(customJobFactory);

                globalScheduler.scheduleJob(jobDetail, jobTrigger);

                globalScheduler.start();
            }
            if (jobClass.contains("Local")) {
                localScheduler.scheduleJob(jobDetail, jobTrigger);
            }

            log.info("Job Scheduled Successfully");

            // Updating the status of the job from unscheduled to active
            job.setJobWorkingStatus("active");
            jobsRepo.save(job);
        } catch (SchedulerException e) {
            log.error("Exception while scheduling job: " + e);
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
    public HashMap passHashMapTriggerKey() {
        return mapOfJobIdAndTriggers;
    }

    // Just for reference purpose we are printing the Hashmap of the Trigger Key.
    public void printHashMap() {
        for (Map.Entry entry : mapOfJobIdAndTriggers.entrySet()) {
            System.out.println("Key: " + entry.getKey() + "  Value: " + entry.getValue());
        }
    }
}