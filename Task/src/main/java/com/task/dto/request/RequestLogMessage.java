package com.task.dto.request;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RequestLogMessage {
    @JsonProperty("service")
    private final String serviceName;

    @JsonProperty("requestId")
    private final String requestId;

    @JsonProperty("timestamp")
    private final Long timestamp;  // Explicitly use Long

    @JsonProperty("clientId")
    private final String clientId;

    public RequestLogMessage(String serviceName, String requestId, Long timestamp, String clientId) {
        this.serviceName = serviceName;
        this.requestId = requestId;
        this.timestamp = timestamp;
        this.clientId = clientId;
    }

    @Override
    public String toString() {
        return "RequestLogMessage{" +
                "service='" + serviceName + '\'' +
                ", requestId='" + requestId + '\'' +
                ", timestamp=" + timestamp +
                ", clientId='" + clientId + '\'' +
                '}';
    }
}
