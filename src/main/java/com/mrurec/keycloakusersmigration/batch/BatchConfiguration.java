package com.mrurec.keycloakusersmigration.batch;

import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
import com.mrurec.keycloakusersmigration.poct.repository.PoctUserRepository;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.DefaultBatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;

import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration extends DefaultBatchConfigurer {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PoctUserRepository poctUserRepository;
    @Autowired
    private BatchProcessor batchProcessor;
    @Autowired
    private BatchWriter batchWriter;
    @Autowired
    private BatchExecutionListener batchExecutionListener;

    @Value("${poct.user.issuer}")
    private String issuer;

    @Bean
    public Job migrateUsersJob() {
        return jobBuilderFactory.get("migrateUsersJob")
                .start(batchStep())
                .listener(batchExecutionListener)
                .build();
    }

    @Bean
    public Step batchStep() {
        return stepBuilderFactory.get("batchStep")
                .<String, List<PoctUser>>chunk(1)
                .reader(itemReader())
                .processor(batchProcessor)
                .writer(batchWriter)
                .build();
    }

    @Bean
    public RepositoryItemReader<String> itemReader() {
        return new RepositoryItemReaderBuilder<String>()
                .name("EmailsReader")
                .repository(poctUserRepository)
                .methodName("findEmailsForBatchProcessing")
                .sorts(Map.of("email", Sort.Direction.ASC))
                .arguments(issuer)
                .build();
    }
}
