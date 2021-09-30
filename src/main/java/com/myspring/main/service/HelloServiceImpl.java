package com.myspring.main.service;

import com.myspring.framework.annotation.Autowired;
import com.myspring.framework.annotation.Component;
import com.myspring.framework.annotation.Scope;
import com.myspring.framework.annotation.Value;

@Component("helloService")
@Scope("prototype")
public class HelloServiceImpl implements HelloService {

    @Value("hello")
    private String text;

    @Value("hello1")
    private String text1;

    @Value("256")
    private int int1;

    @Autowired
    private WrapService wrapService;

    @Override
    public void sayHello() {
        System.out.println(text);
        System.out.println(text1);
        System.out.println(int1);
    }
}
