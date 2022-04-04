package com.rhsphere.rapid.rpc.registry;

/**
 * 	RpcRegistryAbstract
 *
 * 	/rapid-rpc   --->  	rapid-rpc-1.0.0
 * 		/com.rhsphere.rapid.rpc.invoke.consumer.test.HelloService
 * 			/providers
 * 				/192.168.11.101:5678
 * 				/192.168.11.102:5679
 * 			/consumers
 * 				/192.168.11.103
 *
 *
 * 		/com.rhsphere.rapid.rpc.invoke.consumer.test.UserService
 * 			/providers
 * 				/192.168.11.101:5678
 * 				/192.168.11.102:5679
 * 			/consumers
 * 				/192.168.11.103
 *
 */
public abstract class AbstractRpcRegistry {

	protected AbstractRpcRegistry() {
	}

	protected static final String ROOT_PATH = "/rapid-rpc";

	protected static final String ROOT_VALUE = "rapid-rpc-1.0.0";

	protected static final String PROVIDERS_PATH = "/providers";

	protected static final String CONSUMERS_PATH = "/consumers";

}
