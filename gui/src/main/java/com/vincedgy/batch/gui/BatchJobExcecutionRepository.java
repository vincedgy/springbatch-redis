package com.vincedgy.batch.gui;

import com.vincedgy.batch.gui.entity.BatchJobExecution;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.rest.core.annotation.RestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.Optional;

@RepositoryRestResource(collectionResourceRel = "jobs", path = "jobs")
@Repository
@EnableSpringDataWebSupport
public interface BatchJobExcecutionRepository extends PagingAndSortingRepository<BatchJobExecution, Long> { }
