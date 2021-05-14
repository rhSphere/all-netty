package com.rhsphere.netty.flash.the.server;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.StandardCharsets;
import java.util.Date;

/**
 * @author chao.yu
 * chao.yu@dianping.com
 * @date 2018/08/04 06:21.
 */
@Slf4j
public class FirstServerHandler extends ChannelInboundHandlerAdapter {

    private static final String TEXT = "你好，netty!你好，欢迎关注我的微信公众号!";

    // 在接收到客户端发来的数据之后被回调
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        //  msg 参数指的就是 Netty 里面数据读写的载体，
        //  为什么这里不直接是 ByteBuf，而需要我们强转一下，我们后面会分析到
        ByteBuf byteBuf = (ByteBuf) msg;

        log.info("服务端读到数据 -> " + byteBuf.toString(StandardCharsets.UTF_8));

        // 回复数据到客户端
        log.info("服务端写出数据");
        ByteBuf out = getByteBuf(ctx);
        ctx.channel().writeAndFlush(out);
    }

    private ByteBuf getByteBuf(ChannelHandlerContext ctx) {
        byte[] bytes = TEXT.getBytes(StandardCharsets.UTF_8);
        ByteBuf buffer = ctx.alloc().buffer();
        buffer.writeBytes(bytes);
        return buffer;
    }
}
