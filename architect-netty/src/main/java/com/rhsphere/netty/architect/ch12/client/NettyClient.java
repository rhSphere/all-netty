package com.rhsphere.netty.architect.ch12.client;

import com.rhsphere.netty.architect.ch12.codec.NettyMessageDecoder;
import com.rhsphere.netty.architect.ch12.codec.NettyMessageEncoder;
import com.rhsphere.netty.architect.ch12.protocol.NettyConstant;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;

import java.net.InetSocketAddress;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class NettyClient {

	EventLoopGroup group = new NioEventLoopGroup();
	private final ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);

	/**
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		new NettyClient().connect(NettyConstant.PORT, NettyConstant.REMOTEIP);
	}

	public void connect(int port, String host) throws Exception {

		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class)
				.option(ChannelOption.TCP_NODELAY, true)
				.handler(new ChannelInitializer<SocketChannel>() {
					@Override
					public void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new NettyMessageDecoder(1024 * 1024, 4, 4));
						ch.pipeline().addLast("MessageEncoder", new NettyMessageEncoder());
						ch.pipeline().addLast("readTimeoutHandler", new ReadTimeoutHandler(50));
						ch.pipeline().addLast("LoginAuthHandler", new LoginAuthReqHandler());
						ch.pipeline().addLast("HeartBeatHandler", new HeartBeatReqHandler());
						//ch.pipeline().addLast("HeartBeatHandler", new SeverHander());
					}
				});
			ChannelFuture future = b.connect(
				new InetSocketAddress(host, port),
				new InetSocketAddress(NettyConstant.LOCALIP,
					NettyConstant.LOCAL_PORT)).sync();
			System.out.println("Client Start.. ");
			future.channel().closeFuture().sync();
		} finally {
			executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						TimeUnit.SECONDS.sleep(1);
						try {
							connect(NettyConstant.PORT, NettyConstant.REMOTEIP);// 发起重连操作
						} catch (Exception e) {
							e.printStackTrace();
						}
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			});
		}
	}

}
