package com.rhsphere.netty.architect.ch12.client;

import com.rhsphere.netty.architect.ch12.protocol.MessageType;
import com.rhsphere.netty.architect.ch12.struct.Header;
import com.rhsphere.netty.architect.ch12.struct.NettyMessage;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;

public class HeartBeatReqHandler extends ChannelInboundHandlerAdapter {

	private volatile ScheduledFuture<?> heartBeat;

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		NettyMessage message = (NettyMessage) msg;

		if (message.getHeader() != null
			&& message.getHeader().getType() == MessageType.LOGIN_RESP.value()) {

			this.heartBeat = ctx.executor().scheduleAtFixedRate(new HeartBeatTask(ctx), 0, 5000, TimeUnit.MILLISECONDS);

		} else if (message.getHeader() != null
			&& message.getHeader().getType() == MessageType.HEARTBEAT_RESP.value()) {

			System.out.println("Client receive com.rhsphere.netty.server heart beat message : ---> " + message);

		} else {
			ctx.fireChannelRead(msg);
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

		System.err.println("感知到Server端关闭。取消心跳任务");

		cause.printStackTrace();
		if (heartBeat != null) {
			heartBeat.cancel(true);
			heartBeat = null;
		}
		ctx.fireExceptionCaught(cause);
	}

	private static class HeartBeatTask implements Runnable {
		private final ChannelHandlerContext ctx;

		public HeartBeatTask(final ChannelHandlerContext ctx) {
			this.ctx = ctx;
		}

		@Override
		public void run() {
			NettyMessage heatBeat = buildHeatBeat();
			System.out.println("Client send heart beat messsage to com.rhsphere.netty.server : ---> " + heatBeat);
			ctx.writeAndFlush(heatBeat);
		}

		private NettyMessage buildHeatBeat() {
			NettyMessage message = new NettyMessage();
			Header header = new Header();
			header.setType(MessageType.HEARTBEAT_REQ.value());
			message.setHeader(header);
			return message;
		}
	}

}
