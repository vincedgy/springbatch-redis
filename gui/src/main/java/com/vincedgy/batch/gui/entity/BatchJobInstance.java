package com.vincedgy.batch.gui.entity;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.Collection;

@Data
@Entity
@RequiredArgsConstructor
@Table(name="batch_job_instance")
public class BatchJobInstance {
    @Id
    @Column(name="job_instance_id")
    Long id;
    Long version;
    String jobName;
    String jobKey;

    //@OneToMany(mappedBy = "JobInstanceId")
    //Collection<BatchJobExecution> jobs;
}
