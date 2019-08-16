package com.vincedgy.batch.jobImportPersons.steps;

import com.vincedgy.batch.entity.PersonRepository;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class Step1Configuration {

    @Bean
    @StepScope
    public Tasklet deleteAll(PersonRepository r) {
        return (stepContribution, chunkContext) -> {
            r.deleteAll();
            return RepeatStatus.FINISHED;
        };
    }

}
