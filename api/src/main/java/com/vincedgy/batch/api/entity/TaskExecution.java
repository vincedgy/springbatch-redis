package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "task_execution")
public class TaskExecution {
    @Id
    @Column(name = "task_execution_id")
    Long taskExecutionId;
    LocalDateTime startTime;
    LocalDateTime endTime;
    String taskName;
    int exitCode;
    String exitMessage;
    String errorMessage;
    LocalDateTime lastUpdated;
    String externalExecutionId;
    Long parentExecutionId;

    @OneToMany(mappedBy = "taskExecutionId")
    Collection<TaskExecutionParams> params;

}
