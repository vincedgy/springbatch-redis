package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class TaskTaskBatchId implements Serializable {
    private Long taskExecutionId;
    private Long jobExecutionId;

}