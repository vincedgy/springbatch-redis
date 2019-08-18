
package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.util.Collection;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "task_execution_params")
public class TaskExecutionParams {

    @Id
    @Column(name = "task_execution_id")
    Long taskExecutionId;
    String taskParam;
}
