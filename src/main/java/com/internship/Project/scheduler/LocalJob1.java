package com.internship.Project.scheduler;

import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import lombok.extern.slf4j.Slf4j;
import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LocalJob1 implements Job {

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    JobsRepo JobsRepo;

    protected void handle(JobExecutionContext context) throws JobExecutionException {

        log.info("LocalJob1 started");

        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");

        int count = (int) dataMap.get("count");

        Jobs job = JobsRepo.findByJobId(jobId);

        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();


        //
        // This part of the code is for getting previous day's date
        String DATE_FORMAT = "yyyy-MM-dd";
        long oneDayInMilliSeconds = 24 * 60 * 60 * 1000;
        LocalDate localDate = LocalDate.now();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date;

        try {
            date = dateFormat.parse(localDate.toString());

            long previousDayInMilliSeconds = date.getTime() - oneDayInMilliSeconds;

            Date previousDate = new Date(previousDayInMilliSeconds);
            String previousDayDate = dateFormat.format(previousDate);

            //
            // We are now reading the log file and we take in all the data which specifies to the previous date(the date which we calculated and found)

            // remove while running on docker
//            String file = "logs\\projectLogs-"+previousDayDate;
            String file = "E:\\Internship\\Project\\logs\\projectLogs-"+previousDayDate+".txt";

            Path path = Paths.get(file);
            List<String> lines = Files.readAllLines(path);

            List<String> dataStore = new ArrayList<String>();
            for (String i : lines) {
                if (i.contains("start") || i.contains("initial") || i.contains("paused") || i.contains("shut") || i.contains("error")) {
                    dataStore.add(i);
                }
            }


            //
            // This part of the code is to create the file in which we will store the log file with date.
            Scanner scanner = new Scanner(System.in);

            File newFileObj = new File("LocalJob1\\ServerLog_info_" + previousDayDate + ".txt");
            if (newFileObj.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj.getName()));
                for (String i : dataStore) {
                    writer.write(i + "\n");
                }
                writer.close();
                log.info("File created and write completed, Path: " + newFileObj.getAbsolutePath());
            } else {
                System.out.println("Server log file already Exists");
                log.warn(newFileObj.getName() + " - File already present");
                System.out.println("Do you want to overwrite?\nSelect: Yes/No");
                String answer = scanner.nextLine().trim().toLowerCase();
                if (answer.equals("yes")) {
                    log.warn("Overwriting request for created file: " + newFileObj.getName());
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj.getName()));
                    for (String i : dataStore) {
                        writer.write(i + "\n");
                    }
                    writer.close();
                    log.info("Overwrite completed, Path: " + newFileObj.getAbsolutePath());
                } else {
                    log.warn("Overwriting denied, file: " + newFileObj.getName());
                }
            }

            // Code for how the output to be given
            // ....
            // ....
            // (Or we can also have a separate job for this)


            // remove while running on docker
//            jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
            jobsExecutedDetails.setInstanceName("get from docker");
            jobsExecutedDetails.setExecutionStatus("Completed");
            jobsExecutedDetails.setExecutionStatusMessage("Job execution successful");
            jobsExecutedDetails.setJobs(job);

            jobExecutedDetailsRepo.save(jobsExecutedDetails);

            log.info("LocalJob1 Completed, Data update in JobExecutedDetails Table");

            dataMap.put("count", 0);


        } catch (ParseException | IOException e) {
            count++;

            if(count > 5){
                JobExecutionException jobExecutionException = new JobExecutionException("Job Failed");
                jobExecutionException.setUnscheduleAllTriggers(true);

                // remove while running on docker
//                jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
                jobsExecutedDetails.setInstanceName("get from docker");
                jobsExecutedDetails.setExecutionStatus("Job Failed");
                jobsExecutedDetails.setExecutionStatusMessage("Exception due to: "+ e.getMessage());
                jobsExecutedDetails.setJobs(job);

                jobExecutedDetailsRepo.save(jobsExecutedDetails);

                log.error("LocalJob1 failed - Exception while executing job, Data update in JobExecutedDetails Table");
                log.warn("All triggers related to the job are Unscheduled");
                throw jobExecutionException;

            } else {
                log.error("Exception during Local Job1 execution - retrying - Count: " + count + "\n" + e);

                dataMap.put("count", count);

                JobExecutionException jobExecutionException = new JobExecutionException(e);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    log.error("Exception while waiting for Thread to hold before firing Job again", e);
                }

                jobExecutionException.setRefireImmediately(true);
                throw jobExecutionException;

            }
        }
    }

    @Override
    public void execute(JobExecutionContext context) throws JobExecutionException {
        handle(context);
    }
}
