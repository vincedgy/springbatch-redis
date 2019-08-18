package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;

@Data
@Entity
@IdClass(BatchJobExecutionParamsId.class)
@RequiredArgsConstructor
@Table(name="batch_job_execution_params")
public class BatchJobExecutionParams {

    @Id
    Long jobExecutionId;
    @Id
    String typeCd;
    @Id
    String keyName;

    String stringVal;
    LocalDateTime dateVal;
    Long longVal;
    Double doubleVal;
    Character identifying;
}
