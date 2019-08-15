package com.vincedgy.batch;

import lombok.extern.log4j.Log4j2;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Log4j2
@Configuration
public class JobGenericConfiguration {

    @Bean
    public StepExecutionListener stepExecutionListener() {
        return new StepExecutionListener() {
            @Override
            public void beforeStep(final StepExecution stepExecution) {
                log.debug("Starting step " + stepExecution.getStepName());
            }

            @Override
            public ExitStatus afterStep(final StepExecution stepExecution) {
                log.debug("Ending step " + stepExecution.getStepName() + ":" + stepExecution.getSummary());
                return stepExecution.getExitStatus();
            }
        };
    }

}
