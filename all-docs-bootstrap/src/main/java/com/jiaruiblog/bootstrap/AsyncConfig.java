package com.jiaruiblog.bootstrap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * Async configuration for thread pool management.
 * Replaces the default SimpleAsyncTaskExecutor with a configurable ThreadPoolTaskExecutor.
 *
 * @author jiarui.luo
 */
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig {

    @Value("${async.core-pool-size:10}")
    private int corePoolSize;

    @Value("${async.max-pool-size:50}")
    private int maxPoolSize;

    @Value("${async.queue-capacity:200}")
    private int queueCapacity;

    @Value("${async.keep-alive-seconds:60}")
    private int keepAliveSeconds;

    /**
     * Custom ThreadPoolTaskExecutor for async operations.
     * This prevents thread exhaustion under high load.
     */
    @Bean(name = "taskExecutor")
    public Executor taskExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(corePoolSize);
        executor.setMaxPoolSize(maxPoolSize);
        executor.setQueueCapacity(queueCapacity);
        executor.setKeepAliveSeconds(keepAliveSeconds);
        executor.setThreadNamePrefix("async-executor-");
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.setWaitForTasksToCompleteOnShutdown(true);
        executor.setAwaitTerminationSeconds(30);
        executor.initialize();
        return executor;
    }
}
