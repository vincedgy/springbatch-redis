package com.vincedgy.batch.JobImportPersons;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.*;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.FlatFileParseException;
import org.springframework.batch.item.file.LineMapper;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.BeanWrapperFieldSetMapper;
import org.springframework.batch.item.file.mapping.DefaultLineMapper;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.batch.item.file.transform.DelimitedLineTokenizer;
import org.springframework.batch.item.file.transform.FieldSet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Configuration
@EnableBatchProcessing
@Log4j2
public class JobConfiguration {

    final private JobBuilderFactory jbf;
    final private StepBuilderFactory sbf;
    final private String filename;
    final private int chunk;

    @Autowired
    PersonRepository personRepository;

    JobConfiguration(final JobBuilderFactory jbf,
                     final StepBuilderFactory sbf,
                     @Value("${application.jobImportPersons.filename:MOCK_DATA_TEST.csv}") final String filename,
                     @Value("${application.jobImportPersons.chunk:1000}") final int chunk) {
        this.jbf = jbf;
        this.sbf = sbf;
        this.filename = filename;
        this.chunk = chunk;
    }

    //--------------------------------------------------
    // READER
    //--------------------------------------------------

    private FlatFileItemReader<Person> reader(String s) {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(new ClassPathResource(filename))
                .delimited()
                .names(new String[]{"id", "firstName", "lastName", "email", "gender", "ipAddress"})
                .lineMapper(lineMapper())
                .fieldSetMapper(new BeanWrapperFieldSetMapper<>() {{
                    setTargetType(Person.class);
                }})
                .build();
    }

    private LineMapper<Person> lineMapper() {
        final DefaultLineMapper<Person> lineMapper = new DefaultLineMapper<>();
        final DelimitedLineTokenizer lineTokenizer = new DelimitedLineTokenizer();
        final PersonFieldSetMapper fieldSetMapper = new PersonFieldSetMapper();

        lineTokenizer.setDelimiter(",");
        lineTokenizer.setStrict(false);

        lineTokenizer.setNames("id", "firstName", "lastName", "email", "gender", "ipAddress");

        lineMapper.setLineTokenizer(lineTokenizer);
        lineMapper.setFieldSetMapper(fieldSetMapper);

        return lineMapper;
    }

    @Component
    static class PersonFieldSetMapper implements FieldSetMapper<Person> {
        @Override
        public Person mapFieldSet(FieldSet fieldSet) {
            final Person p = new Person();
            p.setId(fieldSet.readLong("id"));
            p.setGender(fieldSet.readString("gender"));
            p.setEmail(fieldSet.readString("email"));
            p.setFirstName(fieldSet.readString("firstName"));
            p.setLastName(fieldSet.readString("lastName"));
            p.setIpAddress(fieldSet.readString("ipAddress"));
            return p;
        }
    }

    @Component
    static class JobListenerImportPersons extends JobExecutionListenerSupport {
        final private PersonRepository repository;

        JobListenerImportPersons(final PersonRepository repository) {
            this.repository = repository;
        }

        @Override
        public void afterJob(final JobExecution jobExecution) {
            if (jobExecution.getStatus() == BatchStatus.COMPLETED) {
                log.info("!!! JOB FINISHED! Time to verify the results");
                AtomicReference<Long> count = new AtomicReference<>(0L);
                repository.findAll().forEach(personRedis -> count.getAndSet(count.get() + 1));
                log.info("Found " + count.toString() + " person(s) in Redis database");
            }
        }
    }


    //--------------------------------------------------
    // STEPs
    //--------------------------------------------------
    @Bean
    public Step step1(PersonRedisItemWriter<PersonRedis> writer, JobParameters jobParameters) {

        return sbf.get("1-InsertFromCSVFileToRedisDb")
                .<Person, PersonRedis>chunk(chunk)
                .reader(reader(jobParameters.getString("fileName")))
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
    JobParameters jobParameters() {
        return new JobParametersBuilder()
                .addString("fileName", filename, true)
                .toJobParameters();
    }

}
