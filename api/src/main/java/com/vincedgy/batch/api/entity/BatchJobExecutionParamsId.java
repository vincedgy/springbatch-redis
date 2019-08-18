package com.vincedgy.batch.api.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.io.Serializable;

@Data
@RequiredArgsConstructor
public class BatchJobExecutionParamsId implements Serializable {
    private Long jobExecutionId;
    private String typeCd;
    private String keyName;
}
