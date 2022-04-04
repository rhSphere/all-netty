package com.rhsphere.rapid.rpc.client;

import com.rhsphere.rapid.rpc.client.proxy.RpcAsyncProxy;
import com.rhsphere.rapid.rpc.client.proxy.RpcProxyImpl;

import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ludepeng
 * @date 2022-04-01 16
 */
public class RpcClient {

    private final Map<Class<?>, Object> syncProxyInstanceMap = new ConcurrentHashMap<>();

    private final Map<Class<?>, Object> asyncProxyInstanceMap = new ConcurrentHashMap<>();

    private String serverAddress;

    private List<String> serverAddressList;

    private long timeout;

    private RpcConnectManager rpcConnectManager;


    public void initClient(String serverAddress, long timeout) {
        this.serverAddress = serverAddress;
        this.timeout = timeout;
        this.rpcConnectManager = new RpcConnectManager();
        connect();
    }

    private void connect() {
        this.rpcConnectManager.connect(this.serverAddress);
    }


    /**
     * initClient: 直接返回对应的代理对象，把RpcConnectManager透传到代理对象中
     *
     * @param <T>            范型
     * @param serverAddress  服务地址
     * @param timeout        延迟
     * @param interfaceClass 调用接口
     * @return RpcProxyImpl 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> T initClient(List<String> serverAddress, long timeout, Class<T> interfaceClass) {
        this.serverAddressList = serverAddress;
        this.timeout = timeout;
        this.rpcConnectManager = new RpcConnectManager();
        this.rpcConnectManager.connect(this.serverAddressList);
        return (T) Proxy.newProxyInstance(
            interfaceClass.getClassLoader(),
            new Class<?>[]{interfaceClass},
            new RpcProxyImpl<>(rpcConnectManager, interfaceClass, timeout)
        );
    }


    /**
     * 同步调用
     *
     * @param interfaceClass 接口名称
     * @param <T>            范型
     * @return 范型
     */
    @SuppressWarnings("unchecked")
    public <T> T invokeSync(Class<T> interfaceClass) {
        return (T) syncProxyInstanceMap.computeIfAbsent(interfaceClass,
            k -> Proxy.newProxyInstance(
                k.getClassLoader(),
                new Class<?>[]{interfaceClass},
                new RpcProxyImpl<>(rpcConnectManager, k, timeout)));
    }


    /**
     * 异步调用
     *
     * @param interfaceClass 接口名称
     * @param <T>            范型
     * @return 代理对象
     */
    @SuppressWarnings("unchecked")
    public <T> RpcAsyncProxy invokeAsync(Class<T> interfaceClass) {

        return (RpcProxyImpl<T>) asyncProxyInstanceMap.computeIfAbsent(interfaceClass,
            k ->
                new RpcProxyImpl<T>(rpcConnectManager, (Class<T>) k, timeout));
    }

    public void updateConnectedServer(List<String> serverAddress) {
        this.serverAddressList = serverAddress;
        this.rpcConnectManager.updateConnectedServer(serverAddress);
    }

    public void stop() {
        this.rpcConnectManager.stop();
    }
}
