package com.myprojects.api_gateway.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ApiEvent {

    private String apiKey;
    private String path;
    private String method;
    private int status;
    private long timestamp;
    private long latency;
    private String message;

}
