package com.rhsphere.rapid.rpc.registry;


import com.rhsphere.rapid.rpc.config.provider.ProviderConfig;
import com.rhsphere.rapid.rpc.constant.SignConstants;
import com.rhsphere.rapid.rpc.utils.FastJsonConvertUtil;
import com.rhsphere.rapid.rpc.zookeeper.ZookeeperClient;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * RpcRegistryProviderService 服务提供者的注册到zookeeper的核心实现类
 */
public class RpcRegistryProviderService extends AbstractRpcRegistry {

	private final ZookeeperClient zookeeperClient;

	public RpcRegistryProviderService(ZookeeperClient zookeeperClient) throws Exception {
		this.zookeeperClient = zookeeperClient;
		//	初始化根节点
		if (!zookeeperClient.checkExists(ROOT_PATH)) {
			zookeeperClient.addPersistentNode(ROOT_PATH, ROOT_VALUE);
		}
	}

	/**
	 * 	/rapid-rpc   --->  	rapid-rpc-1.0.0
	 * 		/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0
	 * 			/providers
	 * 				/192.168.11.101:5678
	 * 				/192.168.11.102:5678
	 * 		/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.1
	 * 			/providers
	 * 				/192.168.11.201:1234
	 * 				/192.168.11.202:1234
	 *
	 */
	public void registry(ProviderConfig providerConfig) throws Exception {
		//	接口命名： com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService
		String interfaceClass = providerConfig.getInterface();
		//	实例对象：HelloServiceImpl
		Object ref = providerConfig.getRef();
		//	接口对应的版本号：1.0.0
		String version = providerConfig.getVersion();

		//	/rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0
		String registryKey = ROOT_PATH + "/" + interfaceClass + ":" + version;

		//	如果当前的path不存在 则进行注册到zookeeper
		if (!zookeeperClient.checkExists(registryKey)) {

			/**
			 * 	@Override
				public String hello(String name) {
					return "hello! " + name;
				}

				 @Override
				 public String hello(User user) {
					 return "hello! " + user.getName();
				 }
			 */
			Method[] methods = ref.getClass().getDeclaredMethods();
			Map<String, String> methodMap = new HashMap<>();

			for (Method method : methods) {
				// 	方法名字
				String methodName = method.getName();
				//	入参类型
				Class<?>[] parameterTypes = method.getParameterTypes();
				StringBuilder methodParameterTypes = new StringBuilder();
				for (Class<?> clazz : parameterTypes) {
					String parameterTypeName = clazz.getName();
					methodParameterTypes.append(parameterTypeName).append(",");
				}

				//	hello@java.lang.String
				//	hello@com.rhsphere.rapid.rpc.invoke.consumer.test.User

				//	自己和大家演示的：hello@com.rhsphere.rapid.rpc.invoke.consumer.test.User,java.lang.String
				String key = methodName + "@" + methodParameterTypes.substring(0, methodParameterTypes.length() - 1);
				methodMap.put(key, key);
			}

			//	持久化操作

			//	key: ==>	/rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0
			//	value: ==> methodMap to json
			zookeeperClient.addPersistentNode(registryKey,
				FastJsonConvertUtil.convertObjectToJSON(methodMap));

			zookeeperClient.addPersistentNode(registryKey + PROVIDERS_PATH, "");
		}

		String address = providerConfig.getAddress();
		String registerInstanceKey = registryKey + PROVIDERS_PATH + SignConstants.DIAGONAL + address;

		Map<String, String> instanceMap = new HashMap<String, String>();
		instanceMap.put("weight", providerConfig.getWeight() + "");

		//	key: /rapid-rpc/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService:1.0.0/providers/127.0.0.1:5678
		//	value: instanceMap to json
		this.zookeeperClient.addEphemeralNode(registerInstanceKey,
			FastJsonConvertUtil.convertObjectToJSON(instanceMap));
	}

}
