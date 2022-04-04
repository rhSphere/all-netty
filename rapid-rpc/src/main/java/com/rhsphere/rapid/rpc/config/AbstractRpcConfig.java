package com.rhsphere.rapid.rpc.config;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.atomic.AtomicInteger;

/**
 * @author ludepeng
 * @since 2022/4/2 4:47 下午
 */
public abstract class AbstractRpcConfig {

	private final AtomicInteger generator = new AtomicInteger(0);
	protected String id;
	protected String interfaceClass = null;
	/**
	 * 服务的调用方(consumer端特有的属性)
	 */
	protected Class<?> proxyClass = null;

	public String getId() {
		if (StringUtils.isBlank(id)) {
			id = "rapid-cfg-gen-" + generator.getAndIncrement();
		}
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getInterface() {
		return this.interfaceClass;
	}

	public void setInterface(String interfaceClass) {
		this.interfaceClass = interfaceClass;
	}

}
