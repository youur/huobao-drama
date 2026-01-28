package com.huobao.drama.api.controller;

import com.huobao.drama.application.service.TaskService;
import com.huobao.drama.common.response.ApiResponse;
import com.huobao.drama.domain.model.AsyncTask;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskService taskService;

    @GetMapping("/{id}")
    public ApiResponse<AsyncTask> getTask(@PathVariable String id) {
        return ApiResponse.success(taskService.getTask(id));
    }

    @GetMapping("/resource/{resourceId}")
    public ApiResponse<List<AsyncTask>> getTasksByResource(@PathVariable String resourceId) {
        return ApiResponse.success(taskService.getTasksByResource(resourceId));
    }
}
