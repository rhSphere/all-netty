package com.rhsphere.netty.flash.the.client;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author chao.yu
 * chao.yu@dianping.com
 * @date 2018/08/04 06:23.
 */
@Slf4j
public class FirstClientHandler extends ChannelInboundHandlerAdapter {

//    ChannelInboundHandlerAdapter 楼主定义handler的时候不应该继承这个吧，
//    这类里面没有释放butebuf，
//    而是应该继承SimpleChannelInboundHandler这个类，这个类中有buytebuf相关的释放操作

    private static final String TEXT = "你好，netty!";


    @Override
    public void channelActive(ChannelHandlerContext ctx) {
        log.info("客户端写出数据");

        // 1.获取数据
        ByteBuf buffer = getByteBuf(ctx);

        // 2.写数据
        ctx.channel().writeAndFlush(buffer);
    }


    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {

        byte[] bytes = TEXT.getBytes(StandardCharsets.UTF_8);

        // 获取到一个 ByteBuf 的内存管理器，这个 内存管理器的作用就是分配一个 ByteBuf
        //把字符串的二进制数据填充到 ByteBuf
        ByteBuf buffer = ctx.alloc().buffer();

        buffer.writeBytes(bytes);

        return buffer;
    }


    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        ByteBuf byteBuf = (ByteBuf) msg;

       log.info("客户端读到数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));
    }
}
