package com.mrurec.keycloakusersmigration;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class KeycloakUsersMigrationApplication implements CommandLineRunner {
    @Autowired
    private Job job;
    @Autowired
    private JobLauncher jobLauncher;

    public static void main(String[] args) {
        System.exit(SpringApplication.exit(SpringApplication.run(KeycloakUsersMigrationApplication.class, args)));
    }

    @Override
    public void run(String... args) throws Exception {
        JobParametersBuilder jobParametersBuilder = new JobParametersBuilder();
        jobLauncher.run(job, jobParametersBuilder.toJobParameters());
    }
}
