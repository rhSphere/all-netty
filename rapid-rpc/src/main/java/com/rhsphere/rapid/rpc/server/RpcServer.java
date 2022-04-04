package com.rhsphere.rapid.rpc.server;

import com.rhsphere.rapid.rpc.codec.RpcDecoder;
import com.rhsphere.rapid.rpc.codec.RpcEncoder;
import com.rhsphere.rapid.rpc.codec.RpcRequest;
import com.rhsphere.rapid.rpc.codec.RpcResponse;
import com.rhsphere.rapid.rpc.config.provider.ProviderConfig;
import com.rhsphere.rapid.rpc.constant.SignConstants;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * @author ludepeng
 * @date 2022-04-02 15
 */
@Slf4j
public class RpcServer {

    private final String serverAddress;

    private final EventLoopGroup bossGroup = new NioEventLoopGroup();

    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    /**
     * interface name
     */
    private final Map<String, Object> handlerMap = new HashMap<>();


    public RpcServer(String serverAddress) throws InterruptedException {
        this.serverAddress = serverAddress;
        this.start();
    }

    private void start() throws InterruptedException {
        ServerBootstrap serverBootstrap = new ServerBootstrap();
        serverBootstrap.group(bossGroup, workerGroup)
            .channel(NioServerSocketChannel.class)
            .option(ChannelOption.SO_BACKLOG, 1024)
            .childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ChannelPipeline cp = ch.pipeline();
                    cp.addLast(new LengthFieldBasedFrameDecoder(65536, 0, 4));
                    cp.addLast(new RpcDecoder(RpcRequest.class));
                    cp.addLast(new RpcEncoder(RpcResponse.class));
                    cp.addLast(new RpcServerHandler(handlerMap));
                }
            });

        String[] array = serverAddress.split(SignConstants.COLON);
        ChannelFuture channelFuture = serverBootstrap.bind(array[0], Integer.parseInt(array[1])).sync();
        channelFuture.addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture future) throws Exception {
                if (future.isSuccess()) {
                    log.info("server success bind to " + serverAddress);
                } else {
                    log.info("server fail bind to " + serverAddress);
                    throw new Exception("server start fail, cause: " + future.cause());
                }
            }
        });

        try {
            channelFuture.await(5000, TimeUnit.MILLISECONDS);
            if (channelFuture.isSuccess()) {
                log.info("start rapid rpc success! ");
            }
        } catch (InterruptedException e) {
            log.error("start rapid rpc occur Interrupted, ex: " + e);
        }
    }

    /**
     * $registerProcessor 程序注册器
     */
    public void registerProcessor(ProviderConfig providerConfig) {
        if (Objects.isNull(providerConfig)) {
            return;
        }
        //key ： providerConfig.insterface (userService接口权限命名)
        //value ： providerConfig.ref (userService接口下的具体实现类 userServiceImpl实例对象)
        handlerMap.put(providerConfig.getInterface(), providerConfig.getRef());
    }

    /**
     * $close
     */
    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }
}
