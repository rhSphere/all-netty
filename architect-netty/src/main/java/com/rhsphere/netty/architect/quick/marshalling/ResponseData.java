package com.rhsphere.netty.architect.quick.marshalling;

import java.io.Serializable;

/**
 * @author ludepeng
 * @since 2022/3/26 10:34 上午
 */
public class ResponseData implements Serializable {

	private static final long serialVersionUID = -6231852018644360658L;

	private String id;

	private String name;

	private String responseMessage;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getResponseMessage() {
		return responseMessage;
	}

	public void setResponseMessage(String responseMessage) {
		this.responseMessage = responseMessage;
	}

}
