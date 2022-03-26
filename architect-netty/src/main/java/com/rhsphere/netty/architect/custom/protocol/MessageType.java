package com.rhsphere.netty.architect.custom.protocol;


public enum MessageType {

	SUCCESS((byte) 0),        // 	业务请求成功
	FAILURE((byte) 1);        // 	业务相应失败

	private final byte value;

	MessageType(byte value) {
		this.value = value;
	}

	public static void main(String[] args) {
		System.out.println(MessageType.SUCCESS.value());
		System.out.println(MessageType.FAILURE.value());
	}

	public byte value() {
		return this.value;
	}
}
