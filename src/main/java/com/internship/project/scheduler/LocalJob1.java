package com.internship.project.scheduler;

import com.internship.project.entity.EmailDetails;
import com.internship.project.entity.Jobs;
import com.internship.project.entity.JobsExecutedDetails;
import com.internship.project.repository.EmailDetailsRepo;
import com.internship.project.repository.JobExecutedDetailsRepo;
import com.internship.project.repository.JobsRepo;

import lombok.extern.slf4j.Slf4j;

import org.quartz.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.*;

/* This class has the necessary functionality that the LocalJob1 needs to perform in here we do the Server data
   retrieval which is from the Log file (Created using a custom Logger) */
@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class LocalJob1 extends QuartzJobBean {

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    EmailDetailsRepo emailDetailsRepo;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MailProperties mailProperties;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        log.info("LocalJob1 started");

        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");
        int count = (int) dataMap.get("count");
        String jobName = (String) dataMap.get("jobName");

        String instanceName = "Docker instance";
        JobDetailsData jobDetailsData = new JobDetailsData();
        instanceName = jobDetailsData.getInstanceName();

        File newFileObj = null;

        try {
            // This part of the code is for getting previous day's date

            String DATE_FORMAT = "yyyy-MM-dd";
            long oneDayInMilliSeconds = 24 * 60 * 60 * 1000;
            LocalDate localDate = LocalDate.now();

            SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
            Date date;

            date = dateFormat.parse(localDate.toString());

            long previousDayInMilliSeconds = date.getTime() - oneDayInMilliSeconds;

            Date previousDate = new Date(previousDayInMilliSeconds);
            String previousDayDate = dateFormat.format(previousDate);

            // We are now reading the log file, and we take in all the data which specifies to the previous date(the date which we calculated and found)

            // remove while running on docker
            String file = "logs/projectLogs-"+previousDayDate+".txt";
//            String file = "E:\\Internship\\Project\\logs\\projectLogs-"+previousDayDate+".txt";

            Path path = Paths.get(file);
            List<String> lines = Files.readAllLines(path);

            List<String> dataStore = new ArrayList<String>();
            String tempLine = "";
            for (String i : lines) {
                tempLine = i.toLowerCase();
                if (tempLine.contains("start") || tempLine.contains("initial") || tempLine.contains("paused") || tempLine.contains("shut") || tempLine.contains("complete") || tempLine.contains("error") || tempLine.contains("exception")) {
                    dataStore.add(i);
                }
            }

            // This part of the code is to create the file in which we will store the log file with date.
            Scanner scanner = new Scanner(System.in);

            newFileObj = new File("LocalJob1\\ServerLog_info_" + previousDayDate + ".txt");
            if (newFileObj.createNewFile()) {
                BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj));
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
                    BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj));
                    for (String i : dataStore) {
                        writer.write(i + "\n");
                    }
                    writer.close();
                    log.info("Overwrite completed, Path: " + newFileObj.getAbsolutePath());
                } else {
                    log.warn("Overwriting denied, File location: " + newFileObj.getAbsolutePath());
                }
            }

            saveJobExecutedDetails(instanceName, "Completed", "Job execution successful", jobId);

            sendEmail("LocalJob1", "Success", jobName, "NA", newFileObj);

            log.info("LocalJob1 Completed");

            dataMap.put("count", 0);

        } catch (ParseException | IOException e) {
            count++;

            if (count > 5) {
                JobExecutionException jobExecutionException = new JobExecutionException(e);
                jobExecutionException.setUnscheduleAllTriggers(true);

                saveJobExecutedDetails(instanceName, "Job Failed", "Exception due to: " + e.getMessage(), jobId);

                sendEmail("LocalJob1", "Failed", jobName, e+"\nStack Trace: "+ Arrays.toString(jobExecutionException.getStackTrace()), newFileObj);

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

    private void saveJobExecutedDetails(String instanceName, String jobExecutionStatus, String jobExecutionMessage, Long jobId) {

        Jobs job = jobsRepo.findByJobId(jobId);

        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();

        jobsExecutedDetails.setInstanceName(instanceName);
        jobsExecutedDetails.setExecutionStatus(jobExecutionStatus);
        jobsExecutedDetails.setExecutionStatusMessage(jobExecutionMessage);
        jobsExecutedDetails.setJobs(job);

        jobExecutedDetailsRepo.save(jobsExecutedDetails);

    }

    private void sendEmail(String jobClass, String jobStatus, String jobName, String exceptionMessage, File file){
        try{

            List<EmailDetails> emailDetailsList = emailDetailsRepo.findByJobClassName(jobClass);

            if(emailDetailsList.isEmpty()){
                log.info("There are no recipients to send the email notification");
            } else {
                String subject = "Reg Job: "+jobName+" , Status: "+jobStatus;
                String body;

                if(exceptionMessage.equals("NA")){
                    body = "Hi,\n\nThis is the job status update.\n\nJob Details\nName: "+jobName+"\nJob Class: "+jobClass+"\nStatus: "+jobStatus+"\n\nThank you!\n";
                } else {
                    body = "Hi,\n\nThis is the job status update.\n\nJob Details\nName: "+jobName+"\nJob Class: "+jobClass+"\nStatus: "+jobStatus+"\nException Message: "+exceptionMessage+"\n\nThank you!\n";
                }

                MimeMessage message = javaMailSender.createMimeMessage();

                for(EmailDetails emailDetails : emailDetailsList){
                    String emailID = emailDetails.getEmailId();

                    MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, true, StandardCharsets.UTF_8.toString());

                    mimeMessageHelper.setFrom(mailProperties.getUsername());
                    mimeMessageHelper.setTo(emailID);
                    mimeMessageHelper.setSubject(subject);
                    mimeMessageHelper.setText(body);
                    if(file!=null){
                        mimeMessageHelper.addAttachment(file.getName(), new File(file.getAbsolutePath()));
                    }


                    javaMailSender.send(message);

                    log.info("Email notification sent");
                }
            }
        }catch (MessagingException exception){
            log.error("Email notification could not be sent\n", exception);
        }
    }
}