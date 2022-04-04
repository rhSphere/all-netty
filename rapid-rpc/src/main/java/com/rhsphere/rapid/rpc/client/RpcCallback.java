package com.rhsphere.rapid.rpc.client;

/**
 * @author ludepeng
 * @date 2022-04-01 15
 */

public interface RpcCallback {

    /**
     * 成功
     *
     * @param result 结果
     */
    void success(Object result);

    /**
     * 失败
     *
     * @param throwable 异常
     */
    void failure(Throwable throwable);
}
