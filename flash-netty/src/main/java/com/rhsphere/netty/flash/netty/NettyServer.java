package com.rhsphere.netty.flash.netty;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.util.AttributeKey;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class NettyServer {

    private static final int BEGIN_PORT = 8000;

    /**
     * 创建一个引导类，
     * 指定线程模型，
     * IO 模型，
     * 连接读写处理逻辑，
     * 绑定端口
     */

    public static void main(String[] args) {
        //boos表示监听端口,创建新连接的线程组,
        NioEventLoopGroup boss = new NioEventLoopGroup();

        //worker表示处理每一条连接的数据读写的线程组
        NioEventLoopGroup worker = new NioEventLoopGroup();

        // 创建引导类 ServerBootstrap进行服务端的启动工作,
        final ServerBootstrap serverBootstrap = new ServerBootstrap();

        final AttributeKey<Object> clientKey = AttributeKey.newInstance("clientKey");

        serverBootstrap
            //通过.group(boos, worker)给引导类配置两大线程定型引导类的线程模型指定服务端的IO模型为NIO,
            .group(boss, worker)

            //通过.channel(NioServerSocketChannel.class)来指定IO模型
            .channel(NioServerSocketChannel.class)

            //handler()用于指定在服务端启动过程中的一些逻辑
            .handler(new ChannelInboundHandlerAdapter() {
                @Override
                public void channelActive(ChannelHandlerContext ctx) throws Exception {
                    super.channelActive(ctx);
                }
            })

            //attr()方法给服务端的channel即NioServerSocketChannel指定一些自定义属性
            //通过channel.attr()取出该属性,给NioServerSocketChannel维护一个map
            .attr(AttributeKey.newInstance("serverName"), "nettyServer")

            //childAttr()方法给每一条连接指定自定义属性,通过channel.attr()取出该属性
            .childAttr(clientKey, "clientValue")

            //option()方法给服务端channel设置一些TCP底层相关的属性:
            //ChannelOption.SO_BACKLOG表示系统用于临时存放已完成三次握手的请求的队列的最大长度,
            // 如果连接建立频繁,服务器处理创建新连接较慢,适当调大该参数
            .option(ChannelOption.SO_BACKLOG, 1024)

            //childOption()方法给每条连接设置一些TCP底层相关的属性:
            // 1.ChannelOption.SO_KEEPALIVE表示是否开启TCP底层心跳机制,true为开启
            // 2.ChannelOption.SO_REUSEADDR表示端口释放后立即就可以被再次使用,因为一般来说,一个端口释放后会等待两分钟之后才能再被使用
            // 3.ChannelOption.TCP_NODELAY表示是否开始Nagle算法,true表示关闭,false表示开启,
            //      通俗地说,如果要求高实时性,有数据发送时就马上发送,就关闭,如果需要减少发送次数减少网络交互就开启
            .childOption(ChannelOption.SO_KEEPALIVE, true)
            .childOption(ChannelOption.TCP_NODELAY, true)

            //childHandler()用于指定处理新连接数据的读写处理逻辑
            //调用childHandler()方法给引导类创建ChannelInitializer定义后续每条连接的数据读写,业务处理逻辑,
            //泛型参数NioSocketChannel是Netty对NIO类型的连接的抽象,而NioServerSocketChannel也是对NIO类型的连接的抽象
            .childHandler(new ChannelInitializer<NioSocketChannel>() {
                protected void initChannel(NioSocketChannel ch) {
                   log.info("{}", ch.attr(clientKey).get());
                }
            });


        bind(serverBootstrap, BEGIN_PORT);
    }

    private static void bind(final ServerBootstrap serverBootstrap, final int port) {

        //serverBootstrap.bind()是异步的方法调用之后是立即返回的,
        //返回值是ChannelFuture,给ChannelFuture添加监听器GenericFutureListener,
        //在GenericFutureListener的operationComplete方法里面监听端口是否绑定成功
        serverBootstrap.bind(port).addListener(future -> {
            if (future.isSuccess()) {
               log.info("端口[{}]绑定成功!", port);
            } else {
                log.warn("端口[{}]绑定失败!", port);
                bind(serverBootstrap, port + 1);
            }
        });
    }
}
