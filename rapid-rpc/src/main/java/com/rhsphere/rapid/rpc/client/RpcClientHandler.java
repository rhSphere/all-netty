package com.rhsphere.rapid.rpc.client;

import com.rhsphere.rapid.rpc.codec.RpcRequest;
import com.rhsphere.rapid.rpc.codec.RpcResponse;
import io.netty.buffer.Unpooled;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.net.SocketAddress;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ludepeng
 * @date 2022-04-01 09
 */
public class RpcClientHandler extends SimpleChannelInboundHandler<RpcResponse> {


    private final Map<String, RpcFuture> pendingRpcTable = new ConcurrentHashMap<>();
    private Channel channel;
    private SocketAddress remotePeer;

    public SocketAddress getRemotePeer() {
        return remotePeer;
    }

    public Channel getChannel() {
        return channel;
    }

    /**
     * 4. 通道关闭
     * Netty提供了一种主动关闭连接的方式.
     * 发送一个Unpooled.EMPTY_BUFFER
     * 这样我们的ChannelFutureListener的close事件就会监听到并关闭通道
     */
    public void close() {
        channel.writeAndFlush(Unpooled.EMPTY_BUFFER).addListener(ChannelFutureListener.CLOSE);
    }

    /**
     * 异步发送请求方法
     *
     * @param request 请求
     * @return future
     */
    public RpcFuture sendRequest(RpcRequest request) {
        RpcFuture future = new RpcFuture(request);
        pendingRpcTable.put(request.getRequestId(), future);
        channel.writeAndFlush(request);
        return future;
    }

    /**
     * 1. 通道注册
     *
     * @param ctx handler上下文
     * @throws Exception 异常
     */
    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        this.channel = ctx.channel();
    }

    /**
     * 2. 通道激活
     *
     * @param ctx handler上下文
     * @throws Exception 异常
     */
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        this.remotePeer = this.channel.remoteAddress();
    }

    /**
     * 3. 核心读取
     *
     * @param ctx handler上下文
     * @throws Exception 异常
     */
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {
        String requestId = response.getRequestId();
        RpcFuture rpcFuture = pendingRpcTable.get(requestId);
        if (rpcFuture != null) {
            pendingRpcTable.remove(requestId);
            rpcFuture.done(response);
        }
    }
}
