package com.vincedgy.batch.api;

import com.vincedgy.batch.api.entity.BatchJobExecution;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(collectionResourceRel = "jobs", path = "jobs")
public interface BatchJobExcecutionRepository extends PagingAndSortingRepository<BatchJobExecution, Long> { }
