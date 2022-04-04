package com.rhsphere.rapid.rpc.client;

import com.rhsphere.rapid.rpc.codec.RpcRequest;
import com.rhsphere.rapid.rpc.codec.RpcResponse;
import com.rhsphere.rapid.rpc.utils.ThreadPoolUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.concurrent.locks.AbstractQueuedSynchronizer;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * @author ludepeng
 * @date 2022-04-01 09
 */
@Slf4j
public class RpcFuture implements Future<Object> {


    private static final long TIME_THRESHOLD = 5000;
    private final Sync sync;
    private final RpcRequest request;
    private final List<RpcCallback> pendingCallbacks = new ArrayList<>();
    private final long startTime;
    private final Lock lock = new ReentrantLock();
    private final ThreadPoolTaskExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolTaskExecutor();
    private RpcResponse response;


    public RpcFuture(RpcRequest request) {
        this.request = request;
        this.startTime = System.currentTimeMillis();
        this.sync = new Sync();
    }

    /**
     * 实际的回调处理
     *
     * @param response 响应
     */
    public void done(RpcResponse response) {
        this.response = response;
        if (sync.release(1)) {
            invokeCallbacks();
        }
        // 整体rpc调用的耗时
        long costTime = System.currentTimeMillis() - startTime;
        if (TIME_THRESHOLD < costTime) {
            log.warn("the rpc response time is too slow, request id = "
                + this.request.getRequestId() + " cost time: " + costTime);
        } else {
            log.info("the rpc response time, request id = "
                + this.request.getRequestId() + " cost time: " + costTime);
        }
    }

    /**
     * 依次执行回调函数处理
     */
    private void invokeCallbacks() {
        lock.lock();
        try {
            for (RpcCallback callback : pendingCallbacks) {
                runCallback(callback);
            }
        } finally {
            lock.unlock();
        }
    }

    private void runCallback(RpcCallback callback) {
        RpcResponse rpcResponse = this.response;
        threadPoolExecutor.submit(() -> {
            if (Objects.isNull(rpcResponse.getThrowable())) {
                callback.success(rpcResponse.getResult());
            } else {
                callback.failure(rpcResponse.getThrowable());
            }
        });
    }

    /**
     * 可以在应用执行的过程中添加回调处理函数
     *
     * @param callback 回掉
     * @return future
     */
    public RpcFuture addCallback(RpcCallback callback) {
        lock.lock();
        try {
            if (isDone()) {
                runCallback(callback);
            } else {
                this.pendingCallbacks.add(callback);
            }
        } finally {
            lock.unlock();
        }
        return this;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning) {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isCancelled() {
        throw new UnsupportedOperationException();
    }

    @Override
    public boolean isDone() {
        return sync.isDone();
    }

    @Override
    public Object get() throws InterruptedException, ExecutionException {
        sync.acquire(-1);
        if (this.response != null) {
            return this.response.getResult();
        }
        return null;
    }

    @Override
    public Object get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
        boolean success = sync.tryAcquireNanos(-1, unit.toNanos(timeout));
        if (success) {
            if (Objects.nonNull(this.response)) {
                return this.response.getResult();
            } else {
                return null;
            }
        } else {
            throw new RuntimeException("timeout execution requestId: "
                + this.request.getRequestId()
                + ",className: " + this.request.getClassName()
                + ",methodName: " + this.request.getMethodName());
        }
    }


    private static class Sync extends AbstractQueuedSynchronizer {
        private static final long serialVersionUID = -3989844522545731058L;

        private static final int DONE = 1;

        private static final int PENDING = 0;


        @Override
        protected boolean tryAcquire(int arg) {
            return getState() == DONE;
        }

        @Override
        protected boolean tryRelease(int arg) {
            if (getState() == PENDING) {
                return compareAndSetState(PENDING, DONE);
            }
            return false;
        }

        public boolean isDone() {
            return getState() == DONE;
        }
    }


}
