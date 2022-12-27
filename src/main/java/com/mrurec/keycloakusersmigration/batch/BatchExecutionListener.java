package com.mrurec.keycloakusersmigration.batch;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Component
public class BatchExecutionListener implements JobExecutionListener {

    @Autowired
    private DataSource dataSource;

    @Override
    public void beforeJob(JobExecution jobExecution) {
        try (Connection connection = dataSource.getConnection()) {
            DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd_HH_mm");
            String backupTableName = "userview_backup_" + LocalDateTime.now().format(dateTimeFormatter);
            connection.prepareStatement(String.format("create table %s as select * from userview", backupTableName)).execute();
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public void afterJob(JobExecution jobExecution) {
        ExitStatus exitStatus = jobExecution.getExitStatus();
        if (ExitStatus.FAILED.equals(exitStatus)) {
            jobExecution.getAllFailureExceptions()
                    .forEach(Throwable::printStackTrace);
            // TODO: 27.12.2022 [yury] ?
        }
    }
}
