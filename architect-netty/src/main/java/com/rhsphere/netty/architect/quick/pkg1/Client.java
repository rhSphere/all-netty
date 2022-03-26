package com.rhsphere.netty.architect.quick.pkg1;

import io.netty.bootstrap.Bootstrap;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.FixedLengthFrameDecoder;
import io.netty.handler.codec.string.StringDecoder;

/**
 * @author ludepeng
 * @date 2022-03-26 10
 */

public class Client {
    public static void main(String[] args) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap bootstrap = new Bootstrap();

        bootstrap.group(group)
            .channel(NioSocketChannel.class)
            .handler(new ChannelInitializer<SocketChannel>() {
                @Override
                protected void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new FixedLengthFrameDecoder(5));
                    ch.pipeline().addLast(new StringDecoder());
                    ch.pipeline().addLast(new ClientHandler());
                }
            });

        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8725).sync();
        channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("aaaaabbbbb".getBytes()));
        Thread.sleep(2000);
        channelFuture.channel().writeAndFlush(Unpooled.wrappedBuffer("cccccbbb".getBytes()));
        channelFuture.channel().closeFuture().sync();
        group.shutdownGracefully();
    }
}
