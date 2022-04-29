package com.internship.Project.scheduler;

import com.internship.Project.entity.CurrencyData;
import com.internship.Project.entity.EmailDetails;
import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.EmailDetailsRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import com.internship.Project.service.Delete;
import com.internship.Project.service.MainService;
import lombok.extern.slf4j.Slf4j;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.mail.MailProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

/* This class has the necessary functionality that the GlobalJob1 needs to perform in here we do the currency data
   retrieval from one particular website */

@Slf4j
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GlobalJob1 extends QuartzJobBean {

//    public GlobalJob1(JobsRepo jobsRepo1) {
//        this.jobsRepo = jobsRepo1;
//    }
//
//    public GlobalJob1(){
//
//    }

    @Autowired
    ApplicationContext applicationContext;

    @Autowired
    Delete delete;

    JobsRepo jobsRepo;

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

    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    CurrencyDataRepo currencyDataRepo;


    @Value("${my.instance.all}")
    private List<String> listInstanceNames;


    private void handle(JobExecutionContext context) throws JobExecutionException {

        for (String i : applicationContext.getBeanDefinitionNames()){
            log.info(i);
        }
        jobsRepo = applicationContext.getBean(JobsRepo.class);

        log.info("GlobalJob1 started");

//        ApplicationContext applicationContext = new ClassPathXmlApplicationContext("applicationContext.xml");
//        MainService mainService = (MainService)applicationContext.getBean("a");
//        jobsRepo = mainService.getJobsRepo();

//        try {
//            jobsRepo = (JobsRepo) context.getScheduler().getContext().get("jobRepository");
//            jobExecutedDetailsRepo = (JobExecutedDetailsRepo) context.getScheduler().getContext().get("jobExecutedDetailsRepository");
//            currencyDataRepo = (CurrencyDataRepo) context.getScheduler().getContext().get("currencyDataRepository");
//        } catch (SchedulerException e) {
//            log.error("Exception while reading from Scheduler context: ", e);
//        }


        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");
        int count = (int) dataMap.get("count");

//        JobDetailsData jobDetailsData = new JobDetailsData();

        final String url = "https://www.x-rates.com/table/?from=INR&amount=1";

        try {
            final Document document = Jsoup.connect(url).get();
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
                }
            }
            // This line of code is used to fetch the date and time when the website data has been updated.

//            final String fetchUpdateDateTime = document.select("span.ratesTimestamp").get(1).text();

            log.info("Currency Data Saved.");

                /* Code for how the output to be given
                ....
                ....
                (Or we can also have a separate job for this) */


            saveJobExecutedDetails("Docker instance", "Completed", "Job execution successful", jobId);

            //sendEmail("GlobalJob1", "Success");

//            JobsExecutedDetails jobsExecuted = jobExecutedDetailsRepo.findTopByOrderByJobsExecutedIdDesc();
//
//            String lastInstance = "lastExecutedInstance";
//
//            String nextInstanceReq = "instance";
//
//
//            if(jobsExecuted == null) {
//
//                nextInstanceReq = listInstanceNames.get(0);
//
//            } else {
//
//                lastInstance = jobsExecuted.getInstanceName();
//
//                int lastInstanceIndex = 0;
//                for(int i = 0; i< listInstanceNames.size(); i++){
//                    if(lastInstance.equals(listInstanceNames.get(i))){
//                        lastInstanceIndex = i;
//                    }
//                }
//
//                if((lastInstanceIndex+1)==listInstanceNames.size()){
//                    nextInstanceReq = listInstanceNames.get(0);
//                } else {
//                    nextInstanceReq = listInstanceNames.get(lastInstanceIndex+1);
//                }
//            }
//
//            jobsRepo.setInstanceNameInDB(nextInstanceReq);

            log.info("GlobalJob1 Completed.");

            dataMap.put("count", 0);

        } catch (IOException e) {
            count++;

            if (count > 5) {
                JobExecutionException jobExecutionException = new JobExecutionException("Job Failed");
                jobExecutionException.setUnscheduleAllTriggers(true);

                saveJobExecutedDetails("Docker instance", "Job Failed", "Error while retrieving GlobalJob1 data from Web", jobId);

                //sendEmail("GlobalJob1", "Success");

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

//
//        if(DataStore.getInstanceName().equals(nextInstanceReq)){
//
//
//        } else {
//            System.out.println("Instance Doesnt Match");
//            JobExecutionException jobExecutionException = new JobExecutionException();
//            jobExecutionException.setRefireImmediately(true);
//
//            throw jobExecutionException;
//        }

        }
    }

//    @Override
//    public void execute(JobExecutionContext context) throws JobExecutionException {
//
//    }

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {
        handle(context);
    }

    private void saveJobExecutedDetails(String instanceName, String jobExecutionStatus, String jobExecutionMessage, Long jobId) {

        Jobs job = jobsRepo.findByJobId(jobId);

        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();

//        jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
        jobsExecutedDetails.setInstanceName(instanceName);
        jobsExecutedDetails.setExecutionStatus(jobExecutionStatus);
        jobsExecutedDetails.setExecutionStatusMessage(jobExecutionMessage);
        jobsExecutedDetails.setJobs(job);

        jobExecutedDetailsRepo.save(jobsExecutedDetails);

    }

//    private void sendEmail(String jobClass, String jobStatus){
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