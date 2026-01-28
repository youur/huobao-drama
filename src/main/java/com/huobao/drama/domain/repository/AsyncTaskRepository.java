package com.huobao.drama.domain.repository;

import com.huobao.drama.domain.model.AsyncTask;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

import java.util.List;

@Repository
public interface AsyncTaskRepository extends JpaRepository<AsyncTask, String> {
    List<AsyncTask> findByResourceIdOrderByCreatedAtDesc(String resourceId);
}
