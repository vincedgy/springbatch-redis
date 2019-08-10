package com.vincedgy.batch.JobImportPersons;

import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonRedisItemWriterDeleteAll<P> implements ItemWriter<String> {
    final private PersonRepository repository;

    PersonRedisItemWriterDeleteAll(final PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(final List<? extends String> list) throws Exception {
        repository.deleteAll();
    }
}
