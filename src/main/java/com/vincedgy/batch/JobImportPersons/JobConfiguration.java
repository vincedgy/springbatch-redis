package com.vincedgy.batch.JobImportPersons;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.*;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;

@Configuration
@EnableBatchProcessing
@Log4j2
public class JobConfiguration {

    final private JobBuilderFactory jbf;
    final private StepBuilderFactory sbf;
    final private int chunk;
    final private String fileName;

    @Autowired
    PersonRepository personRepository;

    JobConfiguration(final JobBuilderFactory jbf,
                     final StepBuilderFactory sbf,
                     @Value("${application.jobImportPersons.filename:MOCK_DATA.csv}") final String fileName,
                     @Value("${application.jobImportPersons.chunk:1000}") final int chunk) {
        this.jbf = jbf;
        this.sbf = sbf;
        this.chunk = chunk;
        this.fileName = fileName;
    }

    //--------------------------------------------------
    // READER
    //--------------------------------------------------

    @Bean
    public FlatFileItemReader<Person> filePersonReader() {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(new ClassPathResource(fileName))
                .delimited()
                .names(new String[]{"id", "firstName", "lastName", "email", "gender", "ipAddress"})
                .fieldSetMapper( fieldSet -> {
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

    //--------------------------------------------------
    // STEPs
    //--------------------------------------------------
    @Bean
    public Step step1(PersonRedisItemWriter<PersonRedis> writer, JobParameters jobParameters) {

        return sbf.get("1-InsertFromCSVFileToRedisDb")
                .<Person, PersonRedis>chunk(chunk)
                .reader(filePersonReader())
                .processor(new PersonProcessor())
                .writer(writer)
                .faultTolerant()
                .skipLimit(10)
                    .skip(FlatFileParseException.class)
                .startLimit(10)
                .build();
    }

    @Bean
    public Step step0(PersonRepository personRepository) {
        return sbf.get("0-RemoveAllData")
                .tasklet((stepContribution, chunkContext) -> {
                    personRepository.deleteAll();
                    return RepeatStatus.FINISHED;
                })
                .startLimit(1)
                .allowStartIfComplete(true)
                .build();
    }

    //--------------------------------------------------
    // Job Definition
    //--------------------------------------------------

    @Bean
    public Job importPersonJob(JobListenerImportPersons listener, Step step0, Step step1, JobParameters jobParameters) {
        return jbf.get("importPersons")
                .incrementer(new RunIdIncrementer())
                .listener(listener)
                .start(step0)
                .next(step1)
                .build();
    }

    @Bean
    JobParameters jobParameters(@Value("${application.jobImportPersons.filename}") final String fileName) {
        return new JobParametersBuilder()
                .addParameter("fileName", new JobParameter(fileName))
                .toJobParameters();
    }

}
