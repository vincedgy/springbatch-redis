package com.vincedgy.batch.JobImportPersons;

import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonRedisItemWriter<P> implements ItemWriter<PersonRedis> {
    @Autowired
    PersonRepository personRepository;

    @Override
    public void write(final List<? extends PersonRedis> list) throws Exception {
        list.forEach(personRepository::save);
    }
}
