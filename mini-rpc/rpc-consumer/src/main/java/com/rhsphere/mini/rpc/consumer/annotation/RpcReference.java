package com.rhsphere.mini.rpc.consumer.annotation;

import org.springframework.beans.factory.annotation.Autowired;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Autowired
public @interface RpcReference {

    /**
     * 服务版本
     *
     * @return 服务版本
     */
    String serviceVersion() default "1.0";

    /**
     * 注册中心类型
     *
     * @return 注册中心类型
     */
    String registryType() default "ZOOKEEPER";

    /**
     * 注册中心地址
     *
     * @return 注册中心地址
     */
    String registryAddress() default "62.234.79.35:2181";

    /**
     * 超时时间
     *
     * @return 超时时间
     */
    long timeout() default 5000;

}
