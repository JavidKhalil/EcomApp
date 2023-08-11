package com.ecom.ecomapp;

import com.ecom.ecomapp.controllers.RequestLimiterController;
import com.ecom.ecomapp.service.RequestLimiterService;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.*;

@WebMvcTest(controllers = RequestLimiterController.class)
class RequestLimiterControllerTest {

    @Value("${app.requestPerXMinutes}")
    private Integer requestPerMinute;

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private RequestLimiterService requestLimiterService;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void contextLoads() {
    }

    @Test
    public void testRequestLimiterEndpoint() throws Exception {
        String expectedResponseEmpty = "_";

        doAnswer(invocation -> {
            CompletableFuture<String> completableFuture = invocation.getArgument(1);
            completableFuture.complete(expectedResponseEmpty);
            return null;
        }).when(requestLimiterService).trackRequest(any(), any());

        mockMvc.perform(MockMvcRequestBuilders.get("/")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.content().string(expectedResponseEmpty));

        verify(requestLimiterService, times(1)).trackRequest(any(), any());
    }

    @Test
    public void testConcurrentRequests() throws Exception {
        int numConcurrentRequests = 10;
        int requestAmount = 50;
        ExecutorService executor = Executors.newFixedThreadPool(numConcurrentRequests);

        String expectedResponseEmpty = "_";
        String expectedResponseBad = "Bad_GATEWAY";

        for (int i = 0; i < requestAmount; i++) {
            final int currentIndex = i;
            doAnswer(invocation -> {
                CompletableFuture<String> completableFuture = invocation.getArgument(1);
                if (currentIndex % 2 == 0) {
                    completableFuture.complete(expectedResponseEmpty);
                } else {
                    completableFuture.complete(expectedResponseBad);
                }
                return null;
            }).when(requestLimiterService).trackRequest(any(), any());

            executor.submit(() -> {
                try {
                    mockMvc.perform(MockMvcRequestBuilders.get("/")
                                    .accept(MediaType.APPLICATION_JSON))
                            .andExpect(MockMvcResultMatchers.status().isOk())
                            .andExpect(MockMvcResultMatchers.content().string(
                                    Matchers.anyOf(
                                            Matchers.is(expectedResponseEmpty),
                                            Matchers.is(expectedResponseBad)
                                    )
                            ));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        }

        // Give some time for the concurrent requests to complete
        Thread.sleep(5000);

        verify(requestLimiterService, times(requestAmount)).trackRequest(any(), any());

        executor.shutdown();
    }
}