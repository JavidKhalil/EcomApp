package com.ecom.ecomapp.service;

import com.ecom.ecomapp.domain.RequestLimiterStatus;
import com.ecom.ecomapp.repository.RequestLimiterRepository;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ForkJoinPool;
import java.util.logging.Logger;

@Service
public class RequestLimiterService {

    private final Logger LOGGER = Logger.getLogger(RequestLimiterService.class.getName());
    /**
     * Empty response.
     */
    private final String EMPTY_RESPONSE = "_";

    private final RequestLimiterRepository requestLimiterRepository;

    public RequestLimiterService(RequestLimiterRepository requestLimiterRepository) {
        this.requestLimiterRepository = requestLimiterRepository;
    }

    public void trackRequest(HttpServletRequest request, CompletableFuture<String> completableFuture) {
        ForkJoinPool.commonPool().execute(() -> {
            String remoteIp = request.getRemoteAddr();
            RequestLimiterStatus status = this.requestLimiterRepository.processRequest(remoteIp);
            completableFuture.complete(computeStatus(status));
            if (completableFuture.isDone()) {
                this.LOGGER.info("Status calculated for ip" + remoteIp);
            }
        });
    }

    private String computeStatus(RequestLimiterStatus status) {
        return status.name().equalsIgnoreCase(RequestLimiterStatus.ALLOWED.name())
                ? EMPTY_RESPONSE
                : HttpStatus.BAD_GATEWAY.name();
    }
}