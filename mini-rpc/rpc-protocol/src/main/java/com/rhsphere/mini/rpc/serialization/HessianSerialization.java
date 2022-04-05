package com.rhsphere.mini.rpc.serialization;

import com.caucho.hessian.io.HessianSerializerInput;
import com.caucho.hessian.io.HessianSerializerOutput;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.Objects;

/**
 * @author ludepeng
 * @date 2022-04-05 19
 */
@Component
@Slf4j
public class HessianSerialization implements RpcSerialization {
    @Override
    public <T> byte[] serialize(T obj) throws IOException {
        if (Objects.isNull(obj)) {
            throw new NullPointerException();
        }
        byte[] results;
        HessianSerializerOutput serializerOutput;
        try (ByteArrayOutputStream bos = new ByteArrayOutputStream()) {
            serializerOutput = new HessianSerializerOutput(bos);
            serializerOutput.writeObject(obj);
            serializerOutput.flush();
            results = bos.toByteArray();
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return results;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T deserialize(byte[] data, Class<T> clz) throws IOException {
        if (Objects.isNull(data)) {
            throw new NullPointerException();
        }
        T result;
        try (ByteArrayInputStream bis = new ByteArrayInputStream(data)) {
            HessianSerializerInput serializerInput = new HessianSerializerInput(bis);
            result = (T) serializerInput.readObject(clz);
        } catch (Exception e) {
            throw new SerializationException(e);
        }
        return result;
    }
}
