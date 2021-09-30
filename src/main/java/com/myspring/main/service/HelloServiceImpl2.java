package com.myspring.main.service;

import com.myspring.framework.annotation.Component;

@Component("helloService2")
public class HelloServiceImpl2 implements HelloService {
    @Override
    public void sayHello() {
        System.out.println("hello service impl2");
    }
}
