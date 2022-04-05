package com.rhphere.mini.rpc.common;

public class RpcServiceHelper {
    public static String buildServiceKey(String serviceName, String serviceVersion) {
        return String.join(SignConstants.WELL_NUMBER, serviceName, serviceVersion);
    }
}
