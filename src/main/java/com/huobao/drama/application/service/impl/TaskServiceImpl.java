package com.huobao.drama.application.service.impl;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.common.exception.BusinessException;
import com.huobao.drama.domain.model.AsyncTask;
import com.huobao.drama.domain.repository.AsyncTaskRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class TaskServiceImpl implements TaskService {

    private final AsyncTaskRepository taskRepository;
    private final ObjectMapper objectMapper;

    @Override
    @Transactional
    public AsyncTask createTask(String type, String resourceId) {
        AsyncTask task = new AsyncTask();
        task.setId(UUID.randomUUID().toString());
        task.setType(type);
        task.setResourceId(resourceId);
        task.setStatus("pending");
        task.setProgress(0);
        return taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskStatus(String taskId, String status, int progress, String message) {
        AsyncTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(404, "Task not found"));
        
        task.setStatus(status);
        task.setProgress(progress);
        task.setMessage(message);
        
        if ("completed".equals(status) || "failed".equals(status)) {
            task.setCompletedAt(LocalDateTime.now());
        }
        
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskError(String taskId, Throwable throwable) {
        AsyncTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(404, "Task not found"));
        
        task.setStatus("failed");
        task.setError(throwable.getMessage());
        task.setProgress(0);
        task.setCompletedAt(LocalDateTime.now());
        
        taskRepository.save(task);
    }

    @Override
    @Transactional
    public void updateTaskResult(String taskId, Object result) {
        AsyncTask task = taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(404, "Task not found"));
        
        try {
            task.setResult(objectMapper.writeValueAsString(result));
            task.setStatus("completed");
            task.setProgress(100);
            task.setCompletedAt(LocalDateTime.now());
            taskRepository.save(task);
        } catch (JsonProcessingException e) {
            log.error("Failed to serialize task result", e);
            updateTaskError(taskId, e);
        }
    }

    @Override
    public AsyncTask getTask(String taskId) {
        return taskRepository.findById(taskId)
                .orElseThrow(() -> new BusinessException(404, "Task not found"));
    }

    @Override
    public List<AsyncTask> getTasksByResource(String resourceId) {
        return taskRepository.findByResourceIdOrderByCreatedAtDesc(resourceId);
    }
}
