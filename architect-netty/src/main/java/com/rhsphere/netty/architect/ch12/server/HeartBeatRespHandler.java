package com.rhsphere.netty.architect.ch12.server;

import com.rhsphere.netty.architect.ch12.protocol.MessageType;
import com.rhsphere.netty.architect.ch12.struct.Header;
import com.rhsphere.netty.architect.ch12.struct.NettyMessage;
import io.netty.channel.ChannelDuplexHandler;
import io.netty.channel.ChannelHandlerContext;


public class HeartBeatRespHandler extends ChannelDuplexHandler {
	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg)
		throws Exception {
		NettyMessage message = (NettyMessage) msg;
		if (message.getHeader() != null
			&& message.getHeader().getType() == MessageType.HEARTBEAT_REQ.value()) {
			System.out.println("Receive com.rhsphere.netty.client heart beat message : ---> " + message);
			NettyMessage heartBeat = buildHeatBeat();
			System.out.println("Send heart beat response message to com.rhsphere.netty.client : ---> " + heartBeat);
			ctx.writeAndFlush(heartBeat);
		} else {
			ctx.fireChannelRead(msg);
		}
	}

	private NettyMessage buildHeatBeat() {
		NettyMessage message = new NettyMessage();
		Header header = new Header();
		header.setType(MessageType.HEARTBEAT_RESP.value());
		message.setHeader(header);
		return message;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.err.println("channelActive..");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.err.println("channelInactive..");
	}

	@Override
	public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
		System.err.println("userEventTriggered..");
		ctx.fireUserEventTriggered(evt);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		System.err.println("感知到Client端关闭, 删除监听");

		cause.printStackTrace();
		ctx.fireExceptionCaught(cause);
	}

}
