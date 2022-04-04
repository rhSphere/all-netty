package com.rhsphere.rapid.rpc.codec;

import io.protostuff.LinkedBuffer;
import io.protostuff.ProtostuffIOUtil;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ludepeng
 * @date 2022-03-31 23
 */

public class Serialization {
    private static final Map<Class<?>, Schema<?>> cachedSchema = new ConcurrentHashMap<>();

    private static final Objenesis objenesis = new ObjenesisStd(true);

    public Serialization() {
    }

    @SuppressWarnings("unchecked")
    private static <T> Schema<T> getSchema(Class<T> cls) {
        return (Schema<T>) cachedSchema.computeIfAbsent(cls, k -> RuntimeSchema.createFrom(cls));
    }


    /**
     * 序列化：对象->字节数组
     *
     * @param obj 对象
     * @param <T> 对象范型
     * @return 二进制数组
     */
    @SuppressWarnings("unchecked")
    public static <T> byte[] serialize(T obj) {
        Class<T> cls = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = getSchema(cls);
            return ProtostuffIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    /**
     * 反序列化（字节数组->对象）
     *
     * @param data 二进制
     * @param cls  类型
     * @param <T>  范型
     * @return 对象
     */
    public static <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            T message = objenesis.newInstance(cls);
            Schema<T> schema = getSchema(cls);
            ProtostuffIOUtil.mergeFrom(data, message, schema);
            return message;
        } catch (Exception e) {
            throw new IllegalStateException(e.getMessage(), e);
        }
    }

}
