package com.rhsphere.netty.flash.netty;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * @description:
 * @author: ludepeng
 * @date: 2021-05-14 15:30
 */
@Slf4j
public class NettyClient {
    private static final int MAX_RETRY = 5;

    /**
     * 创建一个引导类，
     * 指定线程模型，
     * IO 模型，
     * 连接读写处理逻辑，
     * 连接上特定主机和端口
     */

    public static void main(String[] args) {
        NioEventLoopGroup worker = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap
            // 1.指定线程模型
            .group(worker)
            // 2.指定 IO 类型为 NIO
            .channel(NioSocketChannel.class)
            // 绑定自定义属性到 channel
            .attr(AttributeKey.newInstance("clientName"), "nettyClient")
            // 设置TCP底层属性
            .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
            .option(ChannelOption.SO_KEEPALIVE, true)
            .option(ChannelOption.TCP_NODELAY, true)
            // 3.IO 处理逻辑
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) {
                }
            });

        // 4.建立连接
        connect(bootstrap, "juejin.i", 80, MAX_RETRY);
    }

    private static void connect(Bootstrap bootstrap, String host, int port, int retry) {
        bootstrap.connect(host, port).addListener(future -> {
            if (future.isSuccess()) {
                log.info("连接成功!");
            } else if (retry == 0) {
                log.warn("重试次数已用完，放弃连接！");
            } else {
                // 第几次重连
                int order = (MAX_RETRY - retry) + 1;

                // 本次重连的间隔, 通过一个指数退避的方式
                int delay = 1 << order;
                log.warn("{}: 连接失败，第{}次重连……", new Date(), order);

                bootstrap.config().group()
                    .schedule(
                        () -> connect(bootstrap, host, port, retry - 1), delay, TimeUnit.SECONDS);
            }
        });
    }
}
