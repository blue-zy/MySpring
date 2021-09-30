package com.myspring.main.service;

import com.myspring.framework.annotation.Autowired;
import com.myspring.framework.annotation.Component;
import com.myspring.framework.annotation.Qualifier;
import com.myspring.framework.annotation.Scope;

@Component
public class WrapService {

    @Autowired
    @Qualifier("helloService2")
    private HelloService helloService;

    public void say() {
        helloService.sayHello();
    }
}
