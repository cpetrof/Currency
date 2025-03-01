package com.task.repository;

import com.task.model.RequestLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RequestLogRepository extends JpaRepository<RequestLog, String> {
    boolean existsByRequestId(String requestId);

}
