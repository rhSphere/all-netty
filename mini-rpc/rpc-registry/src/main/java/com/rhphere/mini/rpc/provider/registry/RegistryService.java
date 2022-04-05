package com.rhphere.mini.rpc.provider.registry;


import com.rhphere.mini.rpc.common.ServiceMeta;

import java.io.IOException;


/**
 * 通用的注册中心接口
 *
 * @author ludepeng
 * @since 2022/4/5 8:04 下午
 */
public interface RegistryService {

    /**
     * 服务注册
     *
     * @param serviceMeta 服务元数据信息
     * @throws Exception 异常
     */
    void register(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务注销
     *
     * @param serviceMeta 服务元数据信息
     * @throws Exception 异常
     */
    void unRegister(ServiceMeta serviceMeta) throws Exception;

    /**
     * 服务发现
     *
     * @param serviceName     服务名称
     * @param invokerHashCode 调用hashcode
     * @return 服务元数据信息
     * @throws Exception 异常
     */
    ServiceMeta discovery(String serviceName, int invokerHashCode) throws Exception;

    /**
     * 注册中心销毁
     *
     * @throws IOException 异常
     */
    void destroy() throws IOException;
}
