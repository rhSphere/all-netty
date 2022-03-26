package com.rhsphere.netty.architect.ch12.codec;

import org.jboss.marshalling.Marshaller;
import org.jboss.marshalling.MarshallerFactory;
import org.jboss.marshalling.Marshalling;
import org.jboss.marshalling.MarshallingConfiguration;
import org.jboss.marshalling.Unmarshaller;

import java.io.IOException;

public final class MarshallingCodecFactory {

	private MarshallingCodecFactory() {
	}

	/**
	 * 创建Jboss Marshaller
	 *
	 * @return
	 * @throws IOException
	 */
	protected static Marshaller buildMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createMarshaller(configuration);
	}

	/**
	 * 创建Jboss Unmarshaller
	 *
	 * @return
	 * @throws IOException
	 */
	protected static Unmarshaller buildUnMarshalling() throws IOException {
		final MarshallerFactory marshallerFactory = Marshalling.getProvidedMarshallerFactory("serial");
		final MarshallingConfiguration configuration = new MarshallingConfiguration();
		configuration.setVersion(5);
		return marshallerFactory.createUnmarshaller(configuration);
	}
}
