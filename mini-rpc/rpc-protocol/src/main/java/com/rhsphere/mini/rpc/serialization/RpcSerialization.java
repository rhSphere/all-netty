package com.rhsphere.mini.rpc.serialization;

import java.io.IOException;

/**
 * 序列化
 *
 * @author ludepeng
 * @since 2022/4/5 7:00 下午
 */
public interface RpcSerialization {
    /**
     * 序列化接口
     *
     * @param obj 对象
     * @param <T> 范型
     * @return 对象序列化后的字节码
     * @throws IOException 异常
     */
    <T> byte[] serialize(T obj) throws IOException;


    /**
     * 反序列化
     *
     * @param data 二进制字节码
     * @param clz  类型
     * @param <T>  范型
     * @return 反序列化对象
     * @throws IOException 异常
     */
    <T> T deserialize(byte[] data, Class<T> clz) throws IOException;
}