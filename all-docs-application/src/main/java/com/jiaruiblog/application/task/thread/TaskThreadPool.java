package com.jiaruiblog.application.task.thread;

import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;
import org.springframework.util.concurrent.ListenableFuture;
import org.springframework.util.concurrent.ListenableFutureCallback;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author Jarrett Luo
 * @Date 2022/10/20 17:37
 * @Version 1.0
 */
@Slf4j
@Component
public class TaskThreadPool {

    private final ThreadPoolTaskExecutor taskExecutor;
    private final List<MainTask> mainTaskList;

    public TaskThreadPool() {
        this(2, "Task_Thread_%d");
    }

    public TaskThreadPool(Integer threadsNum, String threadNameFormat) {
        ThreadFactory threadFactory = new ThreadFactory() {
            private final AtomicInteger threadNumber = new AtomicInteger(1);
            @Override
            public Thread newThread(Runnable r) {
                Thread thread = new Thread(r);
                thread.setDaemon(true);
                thread.setName(String.format(threadNameFormat, threadNumber.getAndIncrement()));
                return thread;
            }
        };

        this.taskExecutor = new ThreadPoolTaskExecutor();
        taskExecutor.setCorePoolSize(threadsNum);
        taskExecutor.setMaxPoolSize(threadsNum);
        taskExecutor.setKeepAliveSeconds(60);
        taskExecutor.setQueueCapacity(512);
        taskExecutor.setThreadFactory(threadFactory);
        taskExecutor.setRejectedExecutionHandler(new ThreadPoolExecutor.AbortPolicy());
        taskExecutor.setAllowCoreThreadTimeOut(true);
        taskExecutor.initialize();

        mainTaskList = new CopyOnWriteArrayList<>();
    }

    @PreDestroy
    public void shutdown() {
        log.info("关闭 TaskThreadPool 线程池...");
        taskExecutor.shutdown();
        log.info("TaskThreadPool 线程池已关闭");
    }

    public <V> void submit(MainTask mainTask) {
        mainTaskList.add(mainTask);
        ListenableFuture<V> future = (ListenableFuture<V>) taskExecutor.submitListenable(mainTask);

        future.addCallback(new ListenableFutureCallback<>() {
            @Override
            public void onSuccess(V result) {
                mainTask.success();
                mainTaskList.remove(mainTask);
            }

            @Override
            public void onFailure(Throwable ex) {
                mainTask.failed(ex);
                mainTask.fallback();
                mainTaskList.remove(mainTask);
            }
        });
    }
}
