package com.rhsphere.rapid.rpc.invoke.provider.test;


import com.rhsphere.rapid.rpc.config.provider.ProviderConfig;
import com.rhsphere.rapid.rpc.config.provider.RpcServerConfig;
import com.rhsphere.rapid.rpc.registry.RpcRegistryProviderService;
import com.rhsphere.rapid.rpc.zookeeper.CuratorImpl;
import com.rhsphere.rapid.rpc.zookeeper.ZookeeperClient;

import java.util.ArrayList;
import java.util.List;

public class ZKProviderStarter1 {

	public static void main(String[] args) {

		//	服务端启动
		new Thread(() -> {
			try {
				ProviderConfig providerConfig = new ProviderConfig();
				providerConfig.setInterface("com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService");
				HelloServiceImpl helloService = HelloServiceImpl.class.newInstance();
				providerConfig.setRef(helloService);

				List<ProviderConfig> providerConfigs = new ArrayList<>();
				providerConfigs.add(providerConfig);

				//	添加注册中心：实例化client对象，CuratorImpl
				ZookeeperClient zookeeperClient = new CuratorImpl("62.234.79.35:2181,62.234.79.35:2182,62.234.79.35:2183", 10000);
				RpcRegistryProviderService registryProviderService = new RpcRegistryProviderService(zookeeperClient);
				RpcServerConfig rpcServerConfig = new RpcServerConfig(providerConfigs, registryProviderService);
				rpcServerConfig.setPort(8765);
				rpcServerConfig.exporter();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

	}
}
