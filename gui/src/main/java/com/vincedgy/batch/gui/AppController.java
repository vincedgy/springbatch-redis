package com.vincedgy.batch.gui;

import com.vincedgy.batch.gui.entity.BatchJobExecution;
import com.vincedgy.batch.gui.entity.TaskExecution;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Optional;

@RestController
public class AppController {

    private final BatchJobExcecutionRepository jobExcecutionRepository;
    private final TaskJobExcecutionRepository taskJobExcecutionRepository;

    public AppController(final BatchJobExcecutionRepository jobExcecutionRepository,
                         final TaskJobExcecutionRepository taskJobExcecutionRepository) {
        this.jobExcecutionRepository = jobExcecutionRepository;
        this.taskJobExcecutionRepository = taskJobExcecutionRepository;
    }

    @GetMapping("/allJobs")
    public @ResponseBody Iterable<BatchJobExecution> getAllBatchs(@PageableDefault Pageable pageable) {
        return jobExcecutionRepository.findAll();
    }

    @GetMapping("/job/{id}")
    public @ResponseBody Optional<BatchJobExecution> getBatchById(@PathVariable("id") Long id, Pageable pageable) {
        return jobExcecutionRepository.findById(id);
    }

    @GetMapping("/allTasks")
    public @ResponseBody Iterable<TaskExecution> getAllTasks(@PageableDefault Pageable pageable) {
        return taskJobExcecutionRepository.findAll();
    }
}
