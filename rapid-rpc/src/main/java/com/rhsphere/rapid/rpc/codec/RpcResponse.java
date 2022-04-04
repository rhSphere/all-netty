package com.rhsphere.rapid.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ludepeng
 * @date 2022-04-01 09
 */
@Data
public class RpcResponse implements Serializable {

    private static final long serialVersionUID = -7989400623370901861L;

    private String requestId;

    private Object result;

    private Throwable throwable;
}
