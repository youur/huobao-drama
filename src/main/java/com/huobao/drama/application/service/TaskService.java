package com.huobao.drama.application.service;

import com.huobao.drama.domain.model.AsyncTask;
import java.util.List;

public interface TaskService {
    AsyncTask createTask(String type, String resourceId);
    
    void updateTaskStatus(String taskId, String status, int progress, String message);
    
    void updateTaskError(String taskId, Throwable throwable);
    
    void updateTaskResult(String taskId, Object result);
    
    AsyncTask getTask(String taskId);
    
    List<AsyncTask> getTasksByResource(String resourceId);
}
