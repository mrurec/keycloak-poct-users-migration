package com.mrurec.keycloakusersmigration.batch;

import com.mrurec.keycloakusersmigration.keycloak.KeycloakProperties;
import com.mrurec.keycloakusersmigration.poct.model.PoctUser;
import com.mrurec.keycloakusersmigration.poct.repository.PoctUserRepository;
import org.keycloak.OAuth2Constants;
import org.keycloak.admin.client.Keycloak;
import org.keycloak.admin.client.KeycloakBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.BatchConfigurer;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.explore.JobExplorer;
import org.springframework.batch.core.explore.support.JobExplorerFactoryBean;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.launch.support.SimpleJobLauncher;
import org.springframework.batch.core.repository.JobRepository;
import org.springframework.batch.core.repository.support.JobRepositoryFactoryBean;
import org.springframework.batch.item.data.RepositoryItemReader;
import org.springframework.batch.item.data.builder.RepositoryItemReaderBuilder;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.batch.item.database.builder.JpaItemWriterBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.Sort;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;
import java.util.List;
import java.util.Map;

@Configuration
@EnableBatchProcessing
public class BatchConfiguration implements BatchConfigurer {

    @Autowired
    public JobBuilderFactory jobBuilderFactory;

    @Autowired
    public StepBuilderFactory stepBuilderFactory;

    @Autowired
    private PoctUserRepository poctUserRepository;
    @Autowired
    private BatchProcessor batchProcessor;
    @Autowired
    private BatchExecutionListener batchExecutionListener;
    @Autowired
    private EntityManagerFactory entityManagerFactory;
    @Autowired
    private DataSource dataSource;
    private final KeycloakProperties keycloakProperties;
    private JobRepository jobRepository;
    private PlatformTransactionManager transactionManager;
    private JobLauncher jobLauncher;
    private JobExplorer jobExplorer;

    @Value("${poct.user.issuer}")
    private String issuer;

    public BatchConfiguration(KeycloakProperties keycloakProperties) {
        this.keycloakProperties = keycloakProperties;
    }

    @Bean
    public Job migrateUsersJob() {
        return jobBuilderFactory.get("migrateUsersJob")
                .incrementer(new RunIdIncrementer())
                .start(batchStep())
                .listener(batchExecutionListener)
                .build();
    }

    @Bean
    public Step batchStep() {
        return stepBuilderFactory.get("batchStep")
                .allowStartIfComplete(true)
                .transactionManager(getTransactionManager())
                .<String, List<PoctUser>>chunk(1)
                .reader(itemReader())
                .processor(batchProcessor)
                .writer(jpaListItemWriter())
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

    @Bean
    public Keycloak keycloak() {
        return KeycloakBuilder.builder()
                .serverUrl(keycloakProperties.getServerUrl())
                .realm("master")
                .clientId("admin-cli")
                .grantType(OAuth2Constants.PASSWORD)
                .username(keycloakProperties.getAdminUsername())
                .password(keycloakProperties.getAdminPassword())
                .build();
    }

    @Bean
    public JpaListItemWriter<PoctUser> jpaListItemWriter() {
        JpaItemWriter<PoctUser> jpaItemWriter = new JpaItemWriterBuilder<PoctUser>()
                .entityManagerFactory(entityManagerFactory)
                .build();
        return new JpaListItemWriter<>(jpaItemWriter);
    }

    @Override
    public JobRepository getJobRepository() throws Exception {
        if (jobRepository == null) {
            JobRepositoryFactoryBean factory = new JobRepositoryFactoryBean();
            factory.setDataSource(dataSource);
            factory.setTransactionManager(getTransactionManager());
            factory.afterPropertiesSet();
            jobRepository = factory.getObject();
        }
        return jobRepository;
    }

    @Override
    public PlatformTransactionManager getTransactionManager() {
        if (transactionManager == null) {
            final JpaTransactionManager tm = new JpaTransactionManager();
            tm.setEntityManagerFactory(entityManagerFactory);
            tm.afterPropertiesSet();
            transactionManager = tm;
        }
        return transactionManager;
    }

    @Override
    public JobLauncher getJobLauncher() throws Exception {
        if (jobLauncher == null) {
            SimpleJobLauncher jl = new SimpleJobLauncher();
            jl.setJobRepository(getJobRepository());
            jl.afterPropertiesSet();
            jobLauncher = jl;
        }
        return jobLauncher;
    }

    @Override
    public JobExplorer getJobExplorer() throws Exception {
        if (jobExplorer == null) {
            JobExplorerFactoryBean factory = new JobExplorerFactoryBean();
            factory.setDataSource(dataSource);
            factory.afterPropertiesSet();
            jobExplorer = factory.getObject();
        }
        return jobExplorer;
    }
}
