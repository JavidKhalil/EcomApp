package com.ecom.ecomapp.domain;


import java.util.Objects;

public class RequestLimiterCounter {

    private String ip;
    private Integer requestCount;

    public RequestLimiterCounter() {
        this.requestCount = 1;
    }

    public RequestLimiterCounter(String ip) {
        this.ip = ip;
        this.requestCount = 1;
    }

    public Integer getRequestCount() {
        return requestCount;
    }

    @Override
    public String toString() {
        return "RequestLimiterResponse{" +
                "ip='" + ip + '\'' +
                ", requestCount='" + requestCount + '\'' +
                '}';
    }

    public RequestLimiterCounter incrementCountBuild() {
        if (Objects.nonNull(this.requestCount)) {
            this.requestCount += 1;
        } else {
            this.requestCount = 1;
        }
        return this;
    }
}