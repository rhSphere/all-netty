package com.rhsphere.rapid.rpc.config.provider;


import com.rhsphere.rapid.rpc.constant.SignConstants;
import com.rhsphere.rapid.rpc.registry.RpcRegistryProviderService;
import com.rhsphere.rapid.rpc.server.RpcServer;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class RpcServerConfig {

	private final String host = "127.0.0.1";
	private final List<ProviderConfig> providerConfigs;
	protected int port;
	private RpcServer rpcServer = null;

	private RpcRegistryProviderService rpcRegistryProviderService;

	public RpcServerConfig(List<ProviderConfig> providerConfigs) {
		this.providerConfigs = providerConfigs;
	}

	/**
	 * RpcServerConfig
	 *
	 * @param providerConfigs            服务提供方的元数据信息列表
	 * @param rpcRegistryProviderService 注册服务
	 */
	public RpcServerConfig(List<ProviderConfig> providerConfigs,
						   RpcRegistryProviderService rpcRegistryProviderService) {
		this.providerConfigs = providerConfigs;
		this.rpcRegistryProviderService = rpcRegistryProviderService;
	}

	public void exporter() {
		if (rpcServer == null) {
			try {
				this.rpcServer = new RpcServer(host + SignConstants.COLON + port);
			} catch (Exception e) {
				log.error("new rpcServer error", e);
			}
		}
		for (ProviderConfig providerConfig : providerConfigs) {
			try {
				rpcServer.registerProcessor(providerConfig);
			} catch (Exception e) {
				e.printStackTrace();
			}

			//	引入注册中心：

			//	设置当前注册服务(providerConfig)的服务地址
			providerConfig.setAddress(host + SignConstants.COLON + port);
			if (rpcRegistryProviderService != null) {
				try {
					//	把当前的providerConfig里面的元数据信息注册到zookeeper上去
					rpcRegistryProviderService.registry(providerConfig);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		}
	}

	public RpcServer getRpcServer() {
		return rpcServer;
	}

	public void setRpcServer(RpcServer rpcServer) {
		this.rpcServer = rpcServer;
	}

	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}


}
