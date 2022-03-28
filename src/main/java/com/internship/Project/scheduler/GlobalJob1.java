package com.internship.Project.scheduler;

import com.internship.Project.entity.CurrencyData;
import com.internship.Project.entity.Jobs;
import com.internship.Project.entity.JobsExecutedDetails;
import com.internship.Project.repository.CurrencyDataRepo;
import com.internship.Project.repository.JobExecutedDetailsRepo;
import com.internship.Project.repository.JobsRepo;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.quartz.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.List;

@Component
@PersistJobDataAfterExecution
@DisallowConcurrentExecution
public class GlobalJob1 extends QuartzJobBean {

    private static final Logger LOG = LoggerFactory.getLogger(GlobalJob1.class);

    @Autowired
    CurrencyDataRepo currencyDataRepo;

    @Autowired
    JobExecutedDetailsRepo jobExecutedDetailsRepo;

    @Autowired
    JobsRepo jobsRepo;

    @Value("${my.instance.all}")
    private List<String> listInstanceNames;

    @Override
    protected void executeInternal(JobExecutionContext context) throws JobExecutionException {

        LOG.info("GlobalJob1 started");

        JobDataMap dataMap = context.getMergedJobDataMap();
        Long jobId = (Long) dataMap.get("jobId");

        int count = (int) dataMap.get("count");

        Jobs job = jobsRepo.findByJobId(jobId);

        JobsExecutedDetails jobsExecutedDetails = new JobsExecutedDetails();

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
//            // This line of code is used to fetch the date and time when the website data has been updated.
//            final String fetchUpdateDateTime = document.select("span.ratesTimestamp").get(1).text();
//
            LOG.info("Currency Data Saved.");

                /* Code for how the output to be given
                ....
                ....
                (Or we can also have a separate job for this) */


            // remove while running on docker
//            jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
            jobsExecutedDetails.setInstanceName("get from docker");
            jobsExecutedDetails.setExecutionStatus("Completed");
            jobsExecutedDetails.setExecutionStatusMessage("Job execution successful");
            jobsExecutedDetails.setJobs(job);

            jobExecutedDetailsRepo.save(jobsExecutedDetails);

            LOG.info("GlobalJob1 Completed.");

            dataMap.put("count", 0);

        } catch (IOException e) {
            count++;

            if(count > 5){
                JobExecutionException jobExecutionException = new JobExecutionException("Job Failed");
                jobExecutionException.setUnscheduleAllTriggers(true);

                // remove while running on docker
//                jobsExecutedDetails.setInstanceName(DataStore.getInstanceName());
                jobsExecutedDetails.setInstanceName("get from docker");
                jobsExecutedDetails.setExecutionStatus("Job Failed");
                jobsExecutedDetails.setExecutionStatusMessage("Error while retrieving GlobalJob1 data from Web");
                jobsExecutedDetails.setJobs(job);

                jobExecutedDetailsRepo.save(jobsExecutedDetails);

                LOG.error("GlobalJob1 failed - Exception while executing job, Data update in JobExecutedDetails Table");
                LOG.warn("All triggers related to the job are Unscheduled");
                throw jobExecutionException;

            } else {
                LOG.error("Exception during Global Job1 execution - retrying - Count: "+count+"\n"+e);

                dataMap.put("count", count);

                JobExecutionException jobExecutionException = new JobExecutionException(e);

                try {
                    Thread.sleep(10000);
                } catch (InterruptedException ex) {
                    LOG.error("Exception while waiting for Thread to hold before firing Job again", e);
                }

                jobExecutionException.setRefireImmediately(true);
                throw jobExecutionException;
            }
        }

//        JobsExecutedDetails jobsExecuted = jobExecutedDetailsRepo.findTopByOrderByJobsExecutedIdDesc();
//
//        String lastInstance = "lastExecutedInstance";
//
//        String nextInstanceReq = "instance";
//
//
//        if(jobsExecuted == null) {
//
//            nextInstanceReq = listInstanceNames.get(0);
//
//        } else {
//
//            lastInstance = jobsExecuted.getInstanceName();
//
//            int lastInstanceIndex = 0;
//            for(int i = 0; i< listInstanceNames.size(); i++){
//                if(lastInstance.equals(listInstanceNames.get(i))){
//                    lastInstanceIndex = i;
//                }
//            }
//
//            if((lastInstanceIndex+1)==listInstanceNames.size()){
//                nextInstanceReq = listInstanceNames.get(0);
//            } else {
//                nextInstanceReq = listInstanceNames.get(lastInstanceIndex+1);
//            }
//        }
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
