package com.rhsphere.rapid.rpc.client;

import com.rhsphere.rapid.rpc.codec.RpcDecoder;
import com.rhsphere.rapid.rpc.codec.RpcEncoder;
import com.rhsphere.rapid.rpc.codec.RpcRequest;
import com.rhsphere.rapid.rpc.codec.RpcResponse;
import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;

/**
 * @author ludepeng
 * @date 2022-03-31 23
 */
public class RpcClientInitializer extends ChannelInitializer {
    @Override
    protected void initChannel(Channel ch) throws Exception {
        ChannelPipeline cp = ch.pipeline();
        //	编解码的handler
        cp.addLast(new RpcEncoder(RpcRequest.class));
        cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4));
        cp.addLast(new RpcDecoder(RpcResponse.class));
        //	实际的业务处理器RpcClientHandler
        cp.addLast(new RpcClientHandler());
    }
}
