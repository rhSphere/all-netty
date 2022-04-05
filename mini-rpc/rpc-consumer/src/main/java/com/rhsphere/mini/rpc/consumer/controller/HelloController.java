package com.rhsphere.mini.rpc.consumer.controller;

import com.rhsphere.mini.rpc.consumer.annotation.RpcReference;
import com.rhsphere.mini.rpc.provider.facade.HelloFacade;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HelloController {

    @SuppressWarnings({"SpringJavaAutowiredFieldsWarningInspection", "SpringJavaInjectionPointsAutowiringInspection"})
    @RpcReference(serviceVersion = "1.0.0", timeout = 3000)
    private HelloFacade helloFacade;

    @GetMapping(value = "/hello")
    public String sayHello() {
        return helloFacade.hello("mini rpc");
    }
}
