package com.vincedgy.batch.JobImportPersons;

import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonRedisItemWriterDeleteAll<P> implements ItemWriter<String> {
    @Autowired
    PersonRepository personRepository;

    @Override
    public void write(final List<? extends String> list) throws Exception {
        personRepository.deleteAll();
    }
}
