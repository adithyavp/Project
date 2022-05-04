package com.internship.project.scheduler;

import lombok.extern.slf4j.Slf4j;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/* This class is used to store the necessary objects which need to be accessed all over the application, and they are of
*  type static for this reason */
@Slf4j
public class JobDetailsData {

    private String instanceName = "Docker ID";

    public String getInstanceName() {

        try {
            Process process = Runtime.getRuntime().exec("uname -n");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String string;
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
}
