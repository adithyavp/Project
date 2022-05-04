package com.internship.project.scheduler;

import com.internship.project.entity.CurrencyData;
import com.internship.project.entity.EmailDetails;
import com.internship.project.entity.Jobs;
import com.internship.project.entity.JobsExecutedDetails;
import com.internship.project.repository.CurrencyDataRepo;
import com.internship.project.repository.EmailDetailsRepo;
import com.internship.project.repository.JobExecutedDetailsRepo;
import com.internship.project.repository.JobsRepo;

import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* This class has the necessary functionality that the GlobalJob1 needs to perform in here we do the currency data
   retrieval from one particular website */

@Slf4j
@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GlobalJob1 extends QuartzJobBean {

    @Autowired
    JobsRepo jobsRepo;

    @Autowired
    CurrencyDataRepo currencyDataRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    EmailDetailsRepo emailDetailsRepo;

    @Autowired
    JavaMailSender javaMailSender;

    @Autowired
    MailProperties mailProperties;


    @Value("${my.instance.all}")
    private List<String> listInstanceNames;


    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        log.info("GlobalJob1 started");

        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");
        int count = (int) dataMap.get("count");
        String jobName = (String) dataMap.get("jobName");

        String instanceName = "Docker instance";
        JobDetailsData jobDetailsData= new JobDetailsData();
        instanceName = jobDetailsData.getInstanceName();

        final String url = "https://www.x-rates.com/table/?from=INR&amount=1";

        File newFileObj = new File(jobName.toLowerCase()+"Data.txt");

        try {
            final Document document = Jsoup.connect(url).get();
            BufferedWriter writer = new BufferedWriter(new FileWriter(newFileObj));
            writer.write((String.format("%-30s %-22s %-22s\n", "Currency Name", "1.00 INR", "inv. 1.00 INR")));
            for (Element row : document.select("table.tablesorter.ratesTable tr")) {
                if (row.select("td:nth-of-type(1)").text().equals("")) {
                    continue;
                } else {
                    final String currencyName = row.select("td:nth-of-type(1)").text();
                    final String ratePerRupee = row.select("td:nth-of-type(2)").text();
                    final String rupeePerCurrency = row.select("td:nth-of-type(3)").text();

                    CurrencyData currencyData = currencyDataRepo.findByCurrencyName(currencyName);
                    CurrencyData currencyData1 = new CurrencyData();
                    if (currencyData == null) {
                        currencyData1.setCurrencyName(currencyName);
                        currencyData1.setRatePerRupee(ratePerRupee);
                        currencyData1.setRupeePerCurrency(rupeePerCurrency);

                        currencyDataRepo.save(currencyData1);
                    } else {
                        currencyData.setRatePerRupee(ratePerRupee);
                        currencyData.setRupeePerCurrency(rupeePerCurrency);

                        currencyDataRepo.save(currencyData);
                    }
                    writer.write((String.format("\n%-30s %-22s %-22s", currencyName, ratePerRupee, rupeePerCurrency)));
                    //writer.write(currencyName+"\t\t\t\t"+ratePerRupee+"\t\t\t\t"+rupeePerCurrency+"\n");
                }
            }

            final String fetchUpdateDateTime = document.select("span.ratesTimestamp").get(1).text();
            writer.write("\n\nThe data was updated at "+fetchUpdateDateTime);

            writer.close();

            log.info("Currency Data Saved.");

            saveJobExecutedDetails(instanceName, "Completed", "Job execution successful", jobId);

            sendEmail("GlobalJob1", "Success", jobName, "NA", newFileObj);

            log.info("GlobalJob1 Completed.");

            dataMap.put("count", 0);

        } catch (IOException e) {
            count++;

            if (count > 5) {
                JobExecutionException jobExecutionException = new JobExecutionException(e);
                jobExecutionException.setUnscheduleAllTriggers(true);

                saveJobExecutedDetails(instanceName, "Job Failed", "Error while retrieving GlobalJob1 data from Web", jobId);

                sendEmail("GlobalJob1", "Failed", jobName, e+"\nStack Trace: "+ Arrays.toString(jobExecutionException.getStackTrace()), newFileObj);

                log.error("GlobalJob1 failed - Exception while executing job, Data update in JobExecutedDetails Table");
                log.warn("All triggers related to the job are Unscheduled");
                throw jobExecutionException;

            } else {
                log.error("Exception during Global Job1 execution - retrying - Count: " + count + "\n" + e);

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