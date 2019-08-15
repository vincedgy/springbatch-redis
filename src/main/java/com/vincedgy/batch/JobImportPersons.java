package com.vincedgy.batch;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@EnableBatchProcessing
@Configuration
public class JobImportPersons {

    @Bean
    public ItemReader<Person> filePersonReader(@Value("${input}") Resource in) throws Exception {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(in)
                .delimited().delimiter(",").names(new String[]{"id", "firstName", "lastName", "email", "gender", "ipAddress"})
                .fieldSetMapper(fs -> {
                    Person p = new Person();
                    p.setId(fs.readLong("id"));
                    p.setGender(fs.readString("gender"));
                    p.setEmail(fs.readString("email"));
                    p.setFirstName(fs.readString("firstName"));
                    p.setLastName(fs.readString("lastName"));
                    p.setIpAddress(fs.readString("ipAddress"));
                    return p;
                })
                .build();
    }

    @Bean
    public ItemWriter<PersonRedis> redisWriter(PersonRepository personRepository) {
        return list -> list.forEach(personRepository::save);
    }

    @Bean
    public ItemProcessor<Person, PersonRedis> processor() {
        return p -> {
            final PersonRedis pr = new PersonRedis();
            pr.setId(UUID.randomUUID().toString());
            pr.setExtId(p.getId().toString());
            pr.setName(p.getLastName().toUpperCase() + " " + p.getFirstName());
            pr.setGender(p.getGender().equals("Male") ? PersonRedis.Gender.MALE : PersonRedis.Gender.FEMALE);
            return pr;
        };
    }

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
    public Job importPersonJob(final JobBuilderFactory jbf,
                               final StepBuilderFactory sbf,
                               final ItemReader<? extends Person> ir,
                               final ItemProcessor<Person, PersonRedis> processor,
                               final ItemWriter<? super PersonRedis> iw,
                               final JobExecutionListener jel,
                               final StepExecutionListener sel,
                               final PersonRepository personRepository,
                               @Value("${CHUNK:100}") int chunk
    ) {
        /**
         * step0 : remove all data from Redis Collection
         */
        Step step0 = sbf.get("0-RemoveAllData")
                .tasklet((stepContribution, chunkContext) -> {
                    personRepository.deleteAll();
                    return RepeatStatus.FINISHED;
                })
                .startLimit(1)
                .allowStartIfComplete(true)
                .listener(sel)
                .build();


        /**
         * step1 : load all Persons from CSV file to Redis Collection
         */
        Step step1 = sbf.get("1-InsertFromCSVFileToRedisDb")
                .<Person, PersonRedis>chunk(chunk)
                .reader(ir)
                .processor(processor)
                .writer(iw)
                .listener(sel)
                .build();

        log.info("STARTING JOB with a CHUNK of " + chunk);

        return jbf.get("importPersons")
                .incrementer(new RunIdIncrementer())
                .listener(jel)
                    .start(step0)
                    .next(step1)
                .build();
    }

}
