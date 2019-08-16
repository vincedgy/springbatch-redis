package com.vincedgy.batch.gui.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@RequiredArgsConstructor
@Table(name="batch_job_execution_context")
public class BatchJobExecutionContext {
    @Id
    Long jobExecutionId;
    String shortContext;
    String serializedContext;

}
