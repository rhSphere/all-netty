package com.rhsphere.mini.rpc.handler;


import com.rhphere.mini.rpc.common.MiniRpcFuture;
import com.rhphere.mini.rpc.common.MiniRpcRequestHolder;
import com.rhphere.mini.rpc.common.MiniRpcResponse;
import com.rhsphere.mini.rpc.protocol.MiniRpcProtocol;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

public class RpcResponseHandler extends SimpleChannelInboundHandler<MiniRpcProtocol<MiniRpcResponse>> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, MiniRpcProtocol<MiniRpcResponse> msg) {
        long requestId = msg.getHeader().getRequestId();
        MiniRpcFuture<MiniRpcResponse> future = MiniRpcRequestHolder.REQUEST_MAP.remove(requestId);
        future.getPromise().setSuccess(msg.getBody());
    }
}

