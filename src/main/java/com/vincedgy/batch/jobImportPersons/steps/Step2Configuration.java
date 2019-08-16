package com.vincedgy.batch.jobImportPersons.steps;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.FlatFileItemReader;
import org.springframework.batch.item.file.builder.FlatFileItemReaderBuilder;
import org.springframework.batch.item.file.mapping.FieldSetMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.Resource;

import java.util.UUID;

@Configuration
public class Step2Configuration {

    @Bean
    FieldSetMapper<Person> fsm() {
        return fs -> {
            Person p = new Person();
            p.setId(fs.readLong("id"));
            p.setGender(fs.readString("gender"));
            p.setEmail(fs.readString("email"));
            p.setFirstName(fs.readString("firstName"));
            p.setLastName(fs.readString("lastName"));
            p.setIpAddress(fs.readString("ipAddress"));
            return p;
        };
    }

    @Bean
    @StepScope
    public FlatFileItemReader<Person> filePersonReader(final @Value("#{jobParameters['input']}") Resource resource,
                                                       final FieldSetMapper<Person> fsm) {
        return new FlatFileItemReaderBuilder<Person>()
                .name("dataItemReader")
                .resource(resource)
                .delimited().delimiter(",").names("id", "firstName", "lastName", "email", "gender", "ipAddress")
                .targetType(Person.class) // Alternative : .fieldSetMapper(fsm)
                .build();
    }

    @Bean
    @StepScope
    public ItemWriter<PersonRedis> redisWriter(PersonRepository personRepository) {
        return list -> list.forEach(personRepository::save);
    }

    @Bean
    @StepScope
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
}
