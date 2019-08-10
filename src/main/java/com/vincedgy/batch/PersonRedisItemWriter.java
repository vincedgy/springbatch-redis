package com.vincedgy.batch;

import com.vincedgy.batch.entity.PersonRedis;
import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class PersonRedisItemWriter<P> implements ItemWriter<PersonRedis> {
    final private PersonRepository repository;

    PersonRedisItemWriter(final PersonRepository repository) {
        this.repository = repository;
    }

    @Override
    public void write(final List<? extends PersonRedis> list) throws Exception {
        list.forEach(repository::save);
    }
}
