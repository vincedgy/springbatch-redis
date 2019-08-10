package com.vincedgy.batch;

import com.vincedgy.batch.entity.Person;
import com.vincedgy.batch.entity.PersonRedis;
import org.springframework.batch.item.ItemProcessor;

import java.util.UUID;

public class PersonProcessor implements ItemProcessor<Person, PersonRedis> {
    @Override
    public PersonRedis process(final Person person) {
        final PersonRedis returnedPerson = new PersonRedis();
        returnedPerson.setId(UUID.randomUUID().toString());
        returnedPerson.setExtId(person.getId().toString());
        returnedPerson.setName(person.getLastName().toUpperCase() + " " + person.getFirstName());
        returnedPerson.setGender(person.getGender().equals("Male") ? PersonRedis.Gender.MALE : PersonRedis.Gender.FEMALE);
        return returnedPerson;
    }
}

