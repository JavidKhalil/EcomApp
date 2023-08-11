package com.ecom.ecomapp.controllers;

import com.ecom.ecomapp.service.RequestLimiterService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.logging.Logger;

@RestController
public class RequestLimiterController {

    private final Logger LOGGER = Logger.getLogger(RequestLimiterController.class.getName());

    private final RequestLimiterService requestLimiterService;

    public RequestLimiterController(RequestLimiterService requestLimiterService) {
        this.requestLimiterService = requestLimiterService;
    }

    /**
     * GET endpoint for empty response.
     * Request limiter applied.
     *
     * @return empty response or BAD_GATEWAY.
     */
    @GetMapping("/")
    public ResponseEntity<String> requestLimiter(HttpServletRequest request) throws ExecutionException, InterruptedException {
        this.LOGGER.info("Request limiter endpoint");
        CompletableFuture<String> completableFuture = new CompletableFuture<>();
        this.requestLimiterService.trackRequest(request, completableFuture);
        return ResponseEntity.ok(completableFuture.get());
    }

}