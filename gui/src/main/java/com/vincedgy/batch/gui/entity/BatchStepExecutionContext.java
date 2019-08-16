package com.vincedgy.batch.gui.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@RequiredArgsConstructor
@Table(name="batch_step_execution_context")
public class BatchStepExecutionContext {
    @Id
    Long stepExecutionId;
    String shortContext;
    String serializedContext;

    //@OneToOne(mappedBy = "jobExecutionContext")
    //BatchJobExecution job;

}
