package com.rhsphere.rapid.rpc.invoke.consumer.test;


import com.rhsphere.rapid.rpc.config.consumer.ConsumerConfig;
import com.rhsphere.rapid.rpc.config.consumer.RpcClientConfig;
import com.rhsphere.rapid.rpc.registry.RpcRegistryConsumerService;
import com.rhsphere.rapid.rpc.zookeeper.CuratorImpl;
import com.rhsphere.rapid.rpc.zookeeper.ZookeeperClient;

public class ZKConsumerStarter {


	@SuppressWarnings("unchecked")
	public static void main(String[] args) throws Exception {
		ZookeeperClient zookeeperClient = new CuratorImpl("62.234.79.35:2181,62.234.79.35:2182,62.234.79.35:2183", 10000);
		RpcRegistryConsumerService rpcRegistryConsumerService = new RpcRegistryConsumerService(zookeeperClient);
		RpcClientConfig rpcClientConfig = new RpcClientConfig(rpcRegistryConsumerService);

		Thread.sleep(1000);

		ConsumerConfig<HelloService> consumerConfig = (ConsumerConfig<HelloService>) rpcClientConfig.getConsumer(HelloService.class, "1.0.0");
		HelloService helloService = consumerConfig.getProxyInstance();
		String result1 = helloService.hello("baihezhuo1");
		System.err.println(result1);

		String result2 = helloService.hello("baihezhuo2");
		System.err.println(result2);

		String result3 = helloService.hello("baihezhuo3");
		System.err.println(result3);
	}
}
