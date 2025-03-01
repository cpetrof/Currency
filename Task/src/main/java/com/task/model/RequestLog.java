package com.task.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RequestLog {
    @Id
    private String requestId;
    private String clientId;
    private LocalDateTime timestamp;
    private String serviceName;

    public RequestLog(String requestId, String clientId, String serviceName) {
        this.requestId = requestId;
        this.clientId = clientId;
        this.serviceName = serviceName;
        this.timestamp = LocalDateTime.now();
    }

    public RequestLog() {}

    public String getRequestId() {
        return requestId;
    }

    public void setRequestId(String requestId) {
        this.requestId = requestId;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public LocalDateTime getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(LocalDateTime timestamp) {
        this.timestamp = timestamp;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }
}
