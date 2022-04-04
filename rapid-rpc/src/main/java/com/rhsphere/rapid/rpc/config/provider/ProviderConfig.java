package com.rhsphere.rapid.rpc.config.provider;

import com.rhsphere.rapid.rpc.config.AbstractRpcConfig;

/**
 * $ProviderConfig
 * 接口名称
 * 程序对象
 *
 * @author 17475
 */
public class ProviderConfig extends AbstractRpcConfig {

	protected Object ref;

	/**
	 * // ip:port
	 */
	protected String address;

	protected String version = "1.0.0";

	/**
	 * // 权重
	 */
	protected int weight = 1;

	public Object getRef() {
		return ref;
	}

	public void setRef(Object ref) {
		this.ref = ref;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public int getWeight() {
		return weight;
	}

	public void setWeight(int weight) {
		this.weight = weight;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

}
