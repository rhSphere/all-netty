package com.rhsphere.rapid.rpc.invoke.provider.test;


import com.rhsphere.rapid.rpc.config.provider.ProviderConfig;
import com.rhsphere.rapid.rpc.config.provider.RpcServerConfig;

import java.util.ArrayList;
import java.util.List;

public class ProviderStarter {

	public static void main(String[] args) {

		//	服务端启动
		new Thread(() -> {
			try {
				// 每一个具体的服务提供者的配置类
				ProviderConfig providerConfig = new ProviderConfig();
				providerConfig.setInterface("com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService");
				HelloServiceImpl helloService = HelloServiceImpl.class.newInstance();
				providerConfig.setRef(helloService);

				//	把所有的ProviderConfig 添加到集合中
				List<ProviderConfig> providerConfigs = new ArrayList<>();
				providerConfigs.add(providerConfig);

				RpcServerConfig rpcServerConfig = new RpcServerConfig(providerConfigs);
				rpcServerConfig.setPort(8765);
				rpcServerConfig.exporter();

			} catch (Exception e) {
				e.printStackTrace();
			}
		}).start();

	}
}
