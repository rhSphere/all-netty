package com.rhsphere.rapid.rpc.invoke.consumer.test;

import lombok.Data;

@Data
public class User {

	private String id;
	private String name;

	public User() {
	}

	public User(String id, String name) {
		this.id = id;
		this.name = name;
	}

}
