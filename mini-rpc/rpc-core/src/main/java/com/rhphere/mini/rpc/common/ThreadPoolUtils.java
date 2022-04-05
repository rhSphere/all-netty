package com.rhphere.mini.rpc.common;

import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 通用线程池
 *
 * @author ludepeng
 * @date 2022-03-29 22
 */
public class ThreadPoolUtils {

    public static final ThreadPoolTaskExecutor THREAD_POOL_TASK_EXECUTOR = new ThreadPoolTaskExecutor();

    static {
        //线程池核心线程，正常情况下开启的线程数量
        THREAD_POOL_TASK_EXECUTOR.setCorePoolSize(10);
        //总线程数
        THREAD_POOL_TASK_EXECUTOR.setMaxPoolSize(50);
        //当核心线程都在跑任务，还有多余的任务会存到此处
        THREAD_POOL_TASK_EXECUTOR.setQueueCapacity(5000);
        //线程空闲时间
        THREAD_POOL_TASK_EXECUTOR.setKeepAliveSeconds(60);
        //线程名称前缀
        THREAD_POOL_TASK_EXECUTOR.setThreadNamePrefix("rpc-threadPool-");
        //线程池满时，拒绝策略，现在设定为：直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        THREAD_POOL_TASK_EXECUTOR.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());

        THREAD_POOL_TASK_EXECUTOR.initialize();
    }

    private ThreadPoolUtils() {

    }

    public static ThreadPoolTaskExecutor getThreadPoolTaskExecutor() {
        return THREAD_POOL_TASK_EXECUTOR;
    }

}
