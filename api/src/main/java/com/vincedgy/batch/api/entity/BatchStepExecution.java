package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@RequiredArgsConstructor
@Table(name="batch_step_execution")
public class BatchStepExecution {
    @Id
    @Column(name="step_execution_id")
    Long id;
    Long version;
    String stepName;

    Long jobExecutionId;

    //@ManyToOne
    //@JoinColumn(name = "job_execution_id", insertable = false, updatable = false)
    //BatchJobExecution jobExecution;

    LocalDateTime startTime;
    LocalDateTime endTime;
    String status;

    Long commitCount;
    Long readCount;
    Long filterCount;
    Long writeCount;
    Long readSkipCount;
    Long writeSkipCount;
    Long processSkipCount;
    Long rollbackCount;

    String exitCode;
    String exitMessage;
    LocalDateTime lastUpdated;

    @OneToOne
    @JoinColumn(name = "stepExecutionId", referencedColumnName = "stepExecutionId")
    BatchStepExecutionContext context;

}
