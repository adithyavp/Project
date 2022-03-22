package com.internship.Project.scheduler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

public class DataStore {

    private static final Logger LOG = LoggerFactory.getLogger(DataStore.class);

    static String string;
    static Process process;
    static String instanceName = "Docker ID";

    public static String getInstanceName() {

        try {
            process = Runtime.getRuntime().exec("uname -n");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            while((string = bufferedReader.readLine())!=null){
                System.out.println(string);
                instanceName = string;
            }
            try {
                process.waitFor();
            } catch (InterruptedException e) {
                LOG.error(e.getMessage(), e);
            }
            process.exitValue();
            process.destroy();

            LOG.info("Docker instance name read successful");
        } catch (IOException e) {
            LOG.error(e.getMessage(), e);
        }

        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }
}
