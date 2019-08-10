package com.vincedgy.batch;

import com.vincedgy.batch.entity.PersonRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.listener.JobExecutionListenerSupport;
import org.springframework.stereotype.Component;

import java.util.concurrent.atomic.AtomicReference;

@Component
@Log4j2
public class JobExecutionNotificationListener extends JobExecutionListenerSupport {
    final private PersonRepository repository;

    JobExecutionNotificationListener(final PersonRepository repository) {
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
