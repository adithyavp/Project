package com.internship.Project.scheduler;

import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

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

@Component
public class LocalJob1 extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(LocalJob1.class);

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    JobsRepo jobsRepo;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        LOG.info("LocalJob1 started");

        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");

        Jobs job = jobsRepo.findByJobId(jobId);

        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();

        String flag = "0";

        //
        //
        // Check for the file path in Docker container and change it
        //
        //

        String file = "E:\\Internship\\GlobalJob\\globaljob_logs";

        //
        // This part of the code is for getting previous day's date
        String DATE_FORMAT = "yyyy-MM-dd";
        long oneDayInMilliSeconds = 24 * 60 * 60 * 1000;
        LocalDate localDate = LocalDate.now();

        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        Date date = null;
        try {
            date = dateFormat.parse(localDate.toString());

        } catch (ParseException e) {
            flag = "1";
            LOG.error("Error while parsing today's date", e);
        }

        long previousDayInMilliSeconds = date.getTime()-oneDayInMilliSeconds;

        Date previousDate = new Date(previousDayInMilliSeconds);
        String previousDayDate = dateFormat.format(previousDate);

        //
        // We are now reading the log file and we take in all the data which specifies to the previous date(the date which we calculated and found)
        Path path = Paths.get(file);
        List<String> lines = new ArrayList<String>();

        try {
            lines = Files.readAllLines(path);
        } catch (IOException e) {
            flag = "2";
            LOG.error("Error while reading Log File: ", e);
        }

        List<String> dataStore = new ArrayList<String>();
        for(String i : lines){
            if(i.contains(previousDayDate)){
                if(i.contains("start")||i.contains("initial")||i.contains("paused")||i.contains("shut")||i.contains("error")) {
                    dataStore.add(i);
                }
            }
        }
        for(String i: dataStore){
            System.out.println(i);
        }

        // This part of the code is to create the file in which we will store the log file with date.
        Scanner scanner = new Scanner(System.in);
        try {
            File newFileObj = new File("ServerLog_info_"+previousDayDate+".txt");
            if(newFileObj.createNewFile()){
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj.getName()));
                for(String i: dataStore){
                    writer.write(i+"\n");
                }
                writer.close();
                LOG.info("File created and write completed, Path: "+newFileObj.getAbsolutePath());
            } else {
                System.out.println("Server log file already Exists");
                LOG.warn(newFileObj.getName()+" - File already present");
                System.out.println("Do you want to overwrite?\nSelect: Yes/No");
                String answer = scanner.nextLine().trim().toLowerCase();
                if(answer.equals("yes")){
                    LOG.warn("Overwriting request for created file: "+newFileObj.getName());
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj.getName()));
                    for(String i: dataStore){
                        writer.write(i+"\n");
                    }
                    writer.close();
                    LOG.info("Overwrite completed, Path: "+newFileObj.getAbsolutePath());
                } else{
                    LOG.warn("Overwriting denied, file: "+newFileObj.getName());
                }
            }
        } catch (IOException e) {
            flag = "3";
            LOG.error("Error while creating file and storing ServerLog info: ", e);
        }

        //
        // If there is no error then we make an entry to the Jobsexecutedtable and send the details via email
        // If there is an error then we make an entry to the Jobsexecutedtable with the error
        // Email scheduling yet to be done.
        if(flag.equals("0")){

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

            LOG.info("LocalJob1 Completed.");
        } else {

            LOG.error("LocalJob1 failed, Exception while executing ");

            // remove while running on docker
//            jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
            jobsExecutedDetails.setInstanceName("get from docker");
            jobsExecutedDetails.setExecutionStatus("Job Failed");
            jobsExecutedDetails.setJobs(job);

            if(flag.equals("1")) {
                jobsExecutedDetails.setExecutionStatusMessage("Error while parsing date");
            }
            if(flag.equals("2")) {
                jobsExecutedDetails.setExecutionStatusMessage("Error while reading log file");
            }
            if(flag.equals("3")) {
                jobsExecutedDetails.setExecutionStatusMessage("Error while retrieving Job1 data from Web");
            }

            jobExecutedDetailsRepo.save(jobsExecutedDetails);
        }
    }

}
