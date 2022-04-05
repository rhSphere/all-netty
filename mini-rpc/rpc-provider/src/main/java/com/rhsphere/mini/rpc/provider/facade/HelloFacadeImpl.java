package com.rhsphere.mini.rpc.provider.facade;


import com.rhsphere.mini.rpc.provider.annotation.RpcService;

/**
 *
 * @author ludepeng
 * @since 2022/4/5 8:15 下午
 */
@RpcService(serviceInterface = HelloFacade.class, serviceVersion = "1.0.0")
public class HelloFacadeImpl implements HelloFacade {
    @Override
    public String hello(String name) {
        return "hello" + name;
    }
}
