package com.rhphere.mini.rpc.common;

import lombok.Data;

@Data
public class ServiceMeta {

    /**
     * 服务名称
     */
    private String serviceName;

    /**
     * 服务版本号
     */
    private String serviceVersion;

    /**
     * 服务地址
     */
    private String serviceAddress;

    /**
     * 服务端口号
     */
    private int servicePort;

}
