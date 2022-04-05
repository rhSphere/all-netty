package com.rhphere.mini.rpc.provider.registry.loadbalancer;

import java.util.List;

/**
 * 负载均衡
 *
 * @author ludepeng
 * @since 2022/4/5 7:56 下午
 */
public interface ServiceLoadBalancer<T> {
    T select(List<T> servers, int hashCode);
}
