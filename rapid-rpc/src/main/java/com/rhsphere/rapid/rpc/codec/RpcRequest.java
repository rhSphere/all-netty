package com.rhsphere.rapid.rpc.codec;

import lombok.Data;

import java.io.Serializable;

/**
 * @author ludepeng
 * @date 2022-04-01 09
 */
@Data
public class RpcRequest implements Serializable {
    public static final long serialVersionUID = 3424024710707513070L;

    private String requestId;

    private String className;

    private String methodName;

    private Class<?>[] parameterTypes;

    private Object[] parameters;
}
