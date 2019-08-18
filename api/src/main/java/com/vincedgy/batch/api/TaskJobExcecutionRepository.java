package com.vincedgy.batch.api;

import com.vincedgy.batch.api.entity.TaskExecution;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.rest.core.annotation.RepositoryRestResource;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.stereotype.Repository;

@RepositoryRestResource(collectionResourceRel = "tasks", path = "tasks")
@Repository
@EnableSpringDataWebSupport
public interface TaskJobExcecutionRepository extends PagingAndSortingRepository<TaskExecution, Long> {
}
