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
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.util.UUID;
import java.util.concurrent.atomic.AtomicReference;

@Log4j2
@EnableBatchProcessing
@Configuration
public class JobImportPersons {

    @Bean
    public ItemReader<Person> filePersonReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(new ClassPathResource("MOCK_DATA_TEST.csv"))
                .delimited().delimiter(",").names(new String[]{"id", "firstName", "lastName", "email", "gender", "ipAddress"})
                .fieldSetMapper(fieldSet -> {
                    Person p = new Person();
                    p.setId(fieldSet.readLong("id"));
                    p.setGender(fieldSet.readString("gender"));
                    p.setEmail(fieldSet.readString("email"));
                    p.setFirstName(fieldSet.readString("firstName"));
                    p.setLastName(fieldSet.readString("lastName"));
                    p.setIpAddress(fieldSet.readString("ipAddress"));
                    return p;
                })
                .build();
    }

    @Bean
    public Job importPersonJob(JobBuilderFactory jbf,
                               StepBuilderFactory sbf,
                               PersonRepository personRepository
    ) {

        Step step0 = sbf.get("0-RemoveAllData")
                .tasklet((stepContribution, chunkContext) -> {
                    personRepository.deleteAll();
                    return RepeatStatus.FINISHED;
                })
                .startLimit(1)
                .allowStartIfComplete(true)
                .build();

        Step step1 = sbf.get("1-InsertFromCSVFileToRedisDb")
                .<Person, PersonRedis>chunk(100)
                .reader(filePersonReader())
                .processor((ItemProcessor<Person, PersonRedis>) person -> {
                    final PersonRedis returnedPerson = new PersonRedis();
                    returnedPerson.setId(UUID.randomUUID().toString());
                    returnedPerson.setExtId(person.getId().toString());
                    returnedPerson.setName(person.getLastName().toUpperCase() + " " + person.getFirstName());
                    returnedPerson.setGender(person.getGender().equals("Male") ? PersonRedis.Gender.MALE : PersonRedis.Gender.FEMALE);
                    return returnedPerson;
                })
                .writer(list -> list.forEach(personRepository::save))
                .faultTolerant()
                .skipLimit(10)
                .skip(FlatFileParseException.class)
                .startLimit(10)
                .build();

        return jbf.get("importPersons")
                .incrementer(new RunIdIncrementer())
                .listener(new JobExecutionListener() {
                    @Override
                    public void beforeJob(final JobExecution jobExecution) {
                    }

                    @Override
                    public void afterJob(final JobExecution jobExecution) {
                        if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                            log.info("!!! JOB FINISHED! Time to verify the results");
                            AtomicReference<Long> count = new AtomicReference<>(0L);
                            personRepository.findAll().forEach(personRedis -> count.getAndSet(count.get() + 1));
                            log.info("Found " + count.toString() + " person(s) in Redis database");
                        }
                    }
                })
                .start(step0)
                .next(step1)
                .build();
    }

}
