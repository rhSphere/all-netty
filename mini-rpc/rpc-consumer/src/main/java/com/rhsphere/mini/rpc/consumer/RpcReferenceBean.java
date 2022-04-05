package com.rhsphere.mini.rpc.consumer;

import com.rhphere.mini.rpc.provider.registry.RegistryFactory;
import com.rhphere.mini.rpc.provider.registry.RegistryService;
import com.rhphere.mini.rpc.provider.registry.RegistryType;
import org.springframework.beans.factory.FactoryBean;

import java.lang.reflect.Proxy;

/**
 * FactoryBean 是一种特种的工厂 Bean，通过 getObject() 方法返回对象
 *
 * @author ludepeng
 * @since 2022/4/5 8:45 下午
 */
public class RpcReferenceBean implements FactoryBean<Object> {

    private Class<?> interfaceClass;

    private String serviceVersion;

    private String registryType;

    private String registryAddress;

    private long timeout;

    private Object object;

    @Override
    public Object getObject() throws Exception {
        return object;
    }

    @Override
    public Class<?> getObjectType() {
        return interfaceClass;
    }

    /**
     * 使用者来说只是通过 @RpcReference 订阅了服务，并不感知底层调用的细节。对于如何实现 RPC 通信、服务寻址等
     *
     * @throws Exception 异常
     */
    public void init() throws Exception {
        // 生成动态代理对象并赋值给 object

        RegistryService registryService = RegistryFactory.getInstance(this.registryAddress, RegistryType.valueOf(this.registryType));
        // 实现动态代理对象，并通过代理对象完成 RPC 调用
        this.object = Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new RpcInvokerProxy(serviceVersion, timeout, registryService));
    }

    public void setInterfaceClass(Class<?> interfaceClass) {
        this.interfaceClass = interfaceClass;
    }

    public void setServiceVersion(String serviceVersion) {
        this.serviceVersion = serviceVersion;
    }

    public void setRegistryType(String registryType) {
        this.registryType = registryType;
    }

    public void setRegistryAddress(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void setTimeout(long timeout) {
        this.timeout = timeout;
    }
}
