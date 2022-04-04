package com.rhsphere.rapid.rpc.server;

import com.rhsphere.rapid.rpc.codec.RpcRequest;
import com.rhsphere.rapid.rpc.codec.RpcResponse;
import com.rhsphere.rapid.rpc.utils.ThreadPoolUtils;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import net.sf.cglib.reflect.FastMethod;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.lang.reflect.InvocationTargetException;
import java.util.Map;

/**
 * @author ludepeng
 * @date 2022-04-02 15
 */
@Slf4j
public class RpcServerHandler extends SimpleChannelInboundHandler<RpcRequest> {

    private final ThreadPoolTaskExecutor threadPoolExecutor = ThreadPoolUtils.getThreadPoolTaskExecutor();


    private final Map<String, Object> handlerMap;

    public RpcServerHandler(Map<String, Object> handlerMap) {
        this.handlerMap = handlerMap;
    }


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        threadPoolExecutor.execute(() -> {
            RpcResponse response = new RpcResponse();
            response.setRequestId(request.getRequestId());
            try {
                Object result = handle(request);
                response.setResult(result);
            } catch (InvocationTargetException t) {
                response.setThrowable(t);
                log.error("rpc service handle request Throwable: " + t);
            }

            ctx.writeAndFlush(response).addListener(new ChannelFutureListener() {
                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        // afterRpcHook
                    }
                }
            });
        });
    }


    /**
     * $handle 解析request请求并且去通过反射获取具体的本地服务实例后执行具体的方法
     *
     * @param request 请求
     * @return 代理请求结果
     * @throws InvocationTargetException 异常
     */
    private Object handle(RpcRequest request) throws InvocationTargetException {
        String className = request.getClassName();
        Object serviceRef = handlerMap.get(className);
        Class<?> serviceClass = serviceRef.getClass();
        String methodName = request.getMethodName();
        Class<?>[] parameterTypes = request.getParameterTypes();
        Object[] parameters = request.getParameters();

        // JDK reflect

        // Cglib
        FastClass serviceFastClass = FastClass.create(serviceClass);
        FastMethod serviceFastMethod = serviceFastClass.getMethod(methodName, parameterTypes);
        return serviceFastMethod.invoke(serviceRef, parameters);
    }

    /**
     * $exceptionCaught 异常处理关闭连接
     */
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught Throwable: " + cause);
        ctx.close();
    }
}
