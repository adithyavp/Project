package com.internship.Project.scheduler;

import com.internship.Project.entity.CurrencyData;
import com.internship.Project.entity.EmailDetails;
import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.EmailDetailsRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.List;

/* This class is used to store the necessary objects which need to be accessed all over the application, and they are of
*  type static for this reason */
@Slf4j
public class JobDetailsData {


    private String string;
    private Process process;
    private String instanceName = "Docker ID";

    public String getInstanceName() {

        try {
            process = Runtime.getRuntime().exec("uname -n");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while ((string = bufferedReader.readLine()) != null) {
                System.out.println(string);
                instanceName = string;
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                log.error(e.getMessage(), e);
            }
            process.exitValue();
            process.destroy();

            log.info("Docker instance name read successful");
        } catch (IOException e) {
            log.error("Error while retrieving Docker instance name", e);
        }

        return instanceName;
    }


//    @Autowired
//    JobsRepo jobsRepo;
//
//    @Autowired
//    CurrencyDataRepo currencyDataRepo;
//
//    @Autowired
//    JobExecutedDetailsRepo jobExecutedDetailsRepo;
//
//    @Autowired
//    EmailDetailsRepo emailDetailsRepo;
//
//    @Autowired
//    JavaMailSender javaMailSender;
//
//    @Autowired
//    MailProperties mailProperties;
//
//    public void saveCurrencyDetails(String currencyName, String ratePerRupee, String rupeePerCurrency) {
//
//        CurrencyData currencyData = currencyDataRepo.findByCurrencyName(currencyName);
//        CurrencyData currencyData1 = new CurrencyData();
//        if (currencyData == null) {
//            currencyData1.setCurrencyName(currencyName);
//            currencyData1.setRatePerRupee(ratePerRupee);
//            currencyData1.setRupeePerCurrency(rupeePerCurrency);
//
//            currencyDataRepo.save(currencyData1);
//        } else {
//            currencyData.setRatePerRupee(ratePerRupee);
//            currencyData.setRupeePerCurrency(rupeePerCurrency);
//
//            currencyDataRepo.save(currencyData);
//        }
//    }
//
//    public void saveJobExecutedDetails(String instanceName, String jobExecutionStatus, String jobExecutionMessage, Long jobId) {
//
//        Jobs job = jobsRepo.findByJobId(jobId);
//
//        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();
//
////        jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
//        jobsExecutedDetails.setInstanceName("get from docker");
//        jobsExecutedDetails.setExecutionStatus("Completed");
//        jobsExecutedDetails.setExecutionStatusMessage("Job execution successful");
//        jobsExecutedDetails.setJobs(job);
//
//        jobExecutedDetailsRepo.save(jobsExecutedDetails);
//
//    }
//
//    public void sendEmail(String jobClass, String jobStatus){
//        try{
//
//            List<EmailDetails> emailDetailsList = emailDetailsRepo.findByJobClassName(jobClass);
//
//            String subject = "Reg Job: "+jobClass+" , Status: "+jobStatus;
//            String body = "Kindly find the Status of the Job\nJob: "+jobClass+" , Status: "+jobStatus+"\n";
//
//            MimeMessage message = javaMailSender.createMimeMessage();
//
//            for(EmailDetails emailDetails : emailDetailsList){
//                String emailID = emailDetails.getEmailId();
//
//                MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(message, StandardCharsets.UTF_8.toString());
//
//                mimeMessageHelper.setFrom(mailProperties.getUsername());
//                mimeMessageHelper.setTo(emailID);
//                mimeMessageHelper.setSubject(subject);
//                mimeMessageHelper.setText(body);
//
//                javaMailSender.send(message);
//            }
//        }catch (MessagingException exception){
//            log.error("Email notification could not be sent\n", exception);
//        }
//    }
}
