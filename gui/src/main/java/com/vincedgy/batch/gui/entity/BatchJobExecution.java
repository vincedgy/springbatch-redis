package com.vincedgy.batch.gui.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@RequiredArgsConstructor
@Table(name="batch_job_execution")
public class BatchJobExecution {
    @Id
    @Column(name="job_execution_id")
    Long id;
    Long version;

    @ManyToOne
    @JoinColumn(name = "job_execution_id", insertable = false, updatable = false)
    BatchJobInstance JobInstanceId;

    LocalDateTime createTime;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String status;
    String exitCode;
    String exitMessage;
    LocalDateTime lastUpdated;
    String jobConfigurationLocation;

    @OneToMany(mappedBy = "jobExecutionId")
    Collection<BatchJobExecutionParams> params;

    @OneToMany(mappedBy = "jobExecutionId")
    Collection<BatchStepExecution> steps;

    @OneToOne
    @JoinColumn(name = "jobExecutionId", referencedColumnName = "jobExecutionId")
    BatchJobExecutionContext jobExecutionContext;

}
