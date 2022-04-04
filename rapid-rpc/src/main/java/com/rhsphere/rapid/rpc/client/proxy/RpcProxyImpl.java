package com.rhsphere.rapid.rpc.client.proxy;


import com.rhsphere.rapid.rpc.client.RpcClientHandler;
import com.rhsphere.rapid.rpc.client.RpcConnectManager;
import com.rhsphere.rapid.rpc.client.RpcFuture;
import com.rhsphere.rapid.rpc.codec.RpcRequest;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RpcProxyImpl<T> implements InvocationHandler, RpcAsyncProxy {

	private final Class<T> clazz;

	private final long timeout;

	private final RpcConnectManager rpcConnectManager;

	public RpcProxyImpl(RpcConnectManager rpcConnectManager, Class<T> clazz, long timeout) {
		this.clazz = clazz;
		this.timeout = timeout;
		this.rpcConnectManager = rpcConnectManager;
	}

	/**
	 * invoke代理接口调用方式
	 */
	@Override
	public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
		//1.设置请求对象
		RpcRequest request = new RpcRequest();
		request.setRequestId(UUID.randomUUID().toString());
		request.setClassName(method.getDeclaringClass().getName());
		request.setMethodName(method.getName());
		request.setParameterTypes(method.getParameterTypes());
		request.setParameters(args);

		//2.选择一个合适的Client任务处理器
//		RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();
		RpcClientHandler handler = this.rpcConnectManager.chooseHandler();

		//3. 发送真正的客户端请求 返回结果
		RpcFuture future = handler.sendRequest(request);
		return future.get(timeout, TimeUnit.SECONDS);
	}

	/**
	 * $call 异步的代理接口实现, 真正的抱出去RpcFuture 给业务方做实际的回调等待处理
	 */
	@Override
	public RpcFuture call(String funcName, Object... args) {

		//1.设置请求对象
		RpcRequest request = new RpcRequest();
		request.setRequestId(UUID.randomUUID().toString());
		request.setClassName(this.clazz.getName());
		request.setMethodName(funcName);
		request.setParameters(args);
		//	TODO: 对应的方法参数类型应该通过 类类型 + 方法名称 通过反射得到parameterTypes
		Class<?>[] parameterTypes = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			parameterTypes[i] = getClassType(args[i]);
		}
		request.setParameterTypes(parameterTypes);

		//2.选择一个合适的Client任务处理器
//		RpcClientHandler handler = RpcConnectManager.getInstance().chooseHandler();
		RpcClientHandler handler = this.rpcConnectManager.chooseHandler();
		return handler.sendRequest(request);
	}

	private Class<?> getClassType(Object obj) {
		Class<?> classType = obj.getClass();
		String typeName = classType.getName();
		switch (typeName) {
			case "java.lang.Integer":
				return Integer.TYPE;
			case "java.lang.Long":
				return Long.TYPE;
			case "java.lang.Float":
				return Float.TYPE;
			case "java.lang.Double":
				return Double.TYPE;
			case "java.lang.Character":
				return Character.TYPE;
			case "java.lang.Boolean":
				return Boolean.TYPE;
			case "java.lang.Short":
				return Short.TYPE;
			case "java.lang.Byte":
				return Byte.TYPE;
			default:
		}
		return classType;
	}
}
