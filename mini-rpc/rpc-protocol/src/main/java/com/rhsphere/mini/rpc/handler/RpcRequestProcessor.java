package com.rhsphere.mini.rpc.handler;


import com.rhphere.mini.rpc.common.ThreadPoolUtils;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class RpcRequestProcessor {

    public RpcRequestProcessor() {
    }

    private static ThreadPoolTaskExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolTaskExecutor();

    public static void submitRequest(Runnable task) {
//        if (threadPoolExecutor == null) {
//            synchronized (RpcRequestProcessor.class) {
//                if (threadPoolExecutor == null) {
//                    threadPoolExecutor = new ThreadPoolExecutor(10, 10, 60L, TimeUnit.SECONDS, new ArrayBlockingQueue<>(10000));
//                }
//            }
//        }
        threadPoolExecutor.submit(task);
    }
}
