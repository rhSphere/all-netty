package com.rhsphere.rapid.rpc.config;

import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.LifecycleException;
import org.apache.catalina.core.StandardThreadExecutor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurerSupport;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;
import java.util.concurrent.ThreadPoolExecutor;

/**
 * @author ludepeng
 * @since 2022/3/31 11:30 下午
 */
@Slf4j
@Configuration
@EnableAsync(proxyTargetClass = true)
public class AsyncConfig extends AsyncConfigurerSupport {

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        //线程池核心线程，正常情况下开启的线程数量
        executor.setCorePoolSize(10);
        //总线程数
        executor.setMaxPoolSize(50);
        //当核心线程都在跑任务，还有多余的任务会存到此处
        executor.setQueueCapacity(5000);
        //线程空闲时间
        executor.setKeepAliveSeconds(60);
        //线程名称前缀
        executor.setThreadNamePrefix("rpc-async-threadPool-");
        //线程池满时，拒绝策略，现在设定为：直接在 execute 方法的调用线程中运行被拒绝的任务；如果执行程序已关闭，则会丢弃该任务
        executor.setRejectedExecutionHandler(new ThreadPoolExecutor.CallerRunsPolicy());
        executor.initialize();
        return executor;
    }

    @Bean("threadPoolExecutor")
    public Executor getIntegrationExecutor() throws LifecycleException {
        StandardThreadExecutor executor = new StandardThreadExecutor();
        //线程池核心线程，正常情况下开启的线程数量
        executor.setMinSpareThreads(10);
        //总线程数
        executor.setMaxThreads(30);
        //当核心线程都在跑任务，还有多余的任务会存到此处
        executor.setMaxQueueSize(10000);
        //线程名称前缀
        executor.setNamePrefix("rpc-threadPool-");
        executor.setPrestartminSpareThreads(true);
        executor.setDaemon(false);
        executor.start();
        return executor;
    }

}
