package com.vincedgy.batch.jobImportPersons;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import com.vincedgy.batch.jobImportPersons.steps.Step2Configuration;
import com.vincedgy.batch.jobImportPersons.steps.Step1Configuration;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@EnableBatchProcessing
@Configuration
public class JobImportPersons {

    @Bean
    JobExecutionListener jobExecutionListener(PersonRepository personRepository) {
        return new JobExecutionListener() {
            @Override
            public void beforeJob(final JobExecution jobExecution) {
            }

            @Override
            public void afterJob(final JobExecution jobExecution) {
                if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                    log.debug("!!! JOB FINISHED! Time to verify the results");
                    AtomicReference<Long> count = new AtomicReference<>(0L);
                    personRepository.findAll().forEach(personRedis -> count.getAndSet(count.get() + 1));
                    log.info("Job have inserted " + count.toString() + " person(s) in the Redis database");
                }
            }
        };
    }

    @Bean
    JobParametersValidator jobParametersValidator() {
        return jp -> {
            assert jp != null;
            if (Objects.requireNonNull(jp.getString("input")).isEmpty()) {
                throw new JobParametersInvalidException("'input' parameter is mandatory");
            } else if (!jp.getParameters().containsKey("input")) {
                throw new JobParametersInvalidException("'input' parameter is mandatory");
            }
        };
    }

    @Bean
    Job importPersonJob(final JobBuilderFactory jbf,
                        final StepBuilderFactory sbf,
                        final Step1Configuration step1Configuration,
                        final Step2Configuration step2Configuration,
                        final JobExecutionListener jel,
                        final StepExecutionListener sel,
                        final JobParametersValidator jpv,
                        final PersonRepository personRepository,
                        @Value("${CHUNK:100}") int chunk
    ) {
        Step step0 = sbf.get("0-RemoveAllData")
                .tasklet(step1Configuration.deleteAll(personRepository))
                .startLimit(1)
                .allowStartIfComplete(true)
                .listener(sel)
                .build();


        /*
          step1 : load all Persons from CSV file to Redis Collection
         */
        Step step1 = sbf.get("1-InsertFromCSVFileToRedisDb")
                .<Person, PersonRedis>chunk(chunk)
                .reader(step2Configuration.filePersonReader(null, null))
                .processor(step2Configuration.processor())
                .writer(step2Configuration.redisWriter(null))
                .listener(sel)
                .build();

        log.info("STARTING JOB with a CHUNK of " + chunk);

        return jbf.get("importPersons")
                .incrementer(new RunIdIncrementer())
                .validator(jpv)
                .listener(jel)
                .start(step0)
                .next(step1)
                .build();
    }

}
