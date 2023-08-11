package com.ecom.ecomapp.repository;

import com.ecom.ecomapp.domain.RequestLimiterCounter;
import com.ecom.ecomapp.domain.RequestLimiterStatus;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.logging.Logger;

/**
 * In memory DB for store ip
 * addresses and status of the request.
 */
@Component
public class RequestLimiterRepository {

    private final Logger LOGGER = Logger.getLogger(RequestLimiterRepository.class.getName());

    @Value("${app.requestPerXMinutes}")
    private Integer requestPerMinute;

    /**
     * thread safe DB.
     */
    private static ConcurrentHashMap<String, RequestLimiterCounter> requestLimiterDatabase = new ConcurrentHashMap<>();

    /**
     * Process request.
     *
     * @param ip remote ip
     * @return status of the request from ip
     */
    public RequestLimiterStatus processRequest(String ip) {
        this.LOGGER.info("Processing request from ip: " + ip);
        RequestLimiterCounter requestLimiterResponse = requestLimiterDatabase.get(ip);
        if (Objects.nonNull(requestLimiterResponse)) {
            if (requestLimiterResponse.getRequestCount() > requestPerMinute) {
                return processedExceededStatus(ip);
            } else {
                return processAllowedStatus(ip, requestLimiterResponse);
            }
        } else {
            return processAllowedStatus(ip, new RequestLimiterCounter(ip));
        }
    }

    private RequestLimiterStatus processedExceededStatus(String ip) {
        this.LOGGER.warning("Ip: " + ip + " exceeded limit");
        return RequestLimiterStatus.EXCEEDED;
    }

    private RequestLimiterStatus processAllowedStatus(String ip, RequestLimiterCounter requestLimiterResponse) {
        this.LOGGER.warning("Ip: " + ip + " allowed");
        requestLimiterDatabase.put(ip,
                requestLimiterResponse.incrementCountBuild());
        return RequestLimiterStatus.ALLOWED;
    }

    /**
     * Cron for clear in-memory DB.
     */

    @Scheduled(cron = "0 0/${app.requestPerXMinutes} * * * ?")
    private void dbCleaner() {
        this.LOGGER.info("Cron job cleaning in memory DB");
        requestLimiterDatabase.clear();
    }

}