package com.vincedgy.batch.gui;

import com.vincedgy.batch.gui.entity.TaskExecution;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
@Repository
@EnableSpringDataWebSupport
public interface TaskJobExcecutionRepository extends PagingAndSortingRepository<TaskExecution, Long> {
}
