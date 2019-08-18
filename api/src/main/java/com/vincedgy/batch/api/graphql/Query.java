package com.vincedgy.batch.api.graphql;

import com.coxautodev.graphql.tools.GraphQLQueryResolver;
import com.vincedgy.batch.api.BatchJobExcecutionRepository;
import com.vincedgy.batch.api.entity.BatchJobExecution;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Component;

@Log4j2
@Component
public class Query implements GraphQLQueryResolver {

    private BatchJobExcecutionRepository repo;
    public Query(BatchJobExcecutionRepository repo) {
        this.repo = repo;
    }

    public Iterable<BatchJobExecution> findAllJobs() {
        return repo.findAll();
    }

    public Long countJobs() {
        return repo.count();
    }


}
