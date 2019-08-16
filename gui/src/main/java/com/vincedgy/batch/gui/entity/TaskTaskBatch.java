package com.vincedgy.batch.gui.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.persistence.Table;

@Data
@Entity
@RequiredArgsConstructor
@Table(name = "task_task_batch")
@IdClass(TaskTaskBatchId.class)
public class TaskTaskBatch {

  @Id
  Long taskExecutionId;
  @Id
  Long jobExecutionId;

}
