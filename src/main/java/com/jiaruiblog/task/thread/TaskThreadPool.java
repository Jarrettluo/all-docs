package com.jiaruiblog.task.thread;

import com.google.common.util.concurrent.*;
import lombok.extern.slf4j.Slf4j;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.*;

/**
 * @Author Jarrett Luo
 * @Date 2022/10/20 17:37
 * @Version 1.0
 */
@Slf4j
public class TaskThreadPool {

    private final ListeningExecutorService listeningExecutorService;

    private static final TaskThreadPool INSTANCE = new TaskThreadPool(2, "Task_Thread_%d");

    private List<MainTask> mainTaskList;


    private TaskThreadPool(Integer threadsNum, String threadNameFormat) {
        ThreadFactory threadFactory = new ThreadFactoryBuilder()
                .setDaemon(true)
                .setNameFormat(threadNameFormat)
                .build();
        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(threadsNum,
                threadsNum,
                60L,
                TimeUnit.MICROSECONDS,
                new LinkedBlockingQueue<>(512),
                threadFactory,
                new ThreadPoolExecutor.AbortPolicy());
        // 让线程释放，释放资源
        threadPoolExecutor.allowCoreThreadTimeOut(true);
        listeningExecutorService = MoreExecutors.listeningDecorator(threadPoolExecutor);
        mainTaskList = new LinkedList<>();
    }

    public static TaskThreadPool getInstance() {
        return INSTANCE;
    }

    public <V> void submit(MainTask mainTask) {
        mainTaskList.add(mainTask);
        // 使用线程池执行任务工作流
        ListenableFuture<V> future = (ListenableFuture<V>) this.listeningExecutorService.submit(mainTask);

        // 工作流执行完成后，回调，将工作流从执行map中移除
        FutureCallback<V> futureCallback = new FutureCallback<V>() {
            @Override
            public void onSuccess(Object o) {
                mainTask.success();
                mainTaskList.remove(mainTask);
            }

            @Override
            public void onFailure(Throwable throwable) {
                mainTask.failed(throwable);
                mainTask.fallback();
                mainTaskList.remove(mainTask);
            }
        };
        Futures.addCallback(future, futureCallback, this.listeningExecutorService);

    }
}
