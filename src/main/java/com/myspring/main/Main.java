package com.myspring.main;

import com.myspring.framework.context.ApplicationContext;
import com.myspring.framework.context.ClassPathXmlApplicationContext;
import com.myspring.main.service.HelloService;
import com.myspring.main.service.HelloServiceImpl;
import com.myspring.main.service.WrapService;

public class Main {
    public static void main(String[] args) throws Exception {
        testAnnotation();
//        testXml();
    }

    public static void testXml() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application.xml");
        System.out.println("装载完成！");

        for (int i = 0; i < 3; i++) {
            HelloService helloService = (HelloService) context.getBean(HelloServiceImpl.class);
            System.out.println(helloService);
            helloService.sayHello();
        }

        System.out.println("-------------------");
        for (int i = 0; i < 3; i++) {
            WrapService wrapService = (WrapService) context.getBean(WrapService.class);
            System.out.println(wrapService);
            wrapService.say();
        }
    }

    public static void testAnnotation() throws Exception {
        ApplicationContext context = new ClassPathXmlApplicationContext("application-annotation.xml");
        System.out.println("装载完成！");

        for (int i = 0; i < 3; i++) {
            HelloService helloService = (HelloService) context.getBean(HelloServiceImpl.class);
            System.out.println(helloService);
            helloService.sayHello();
        }

        System.out.println("-------------------");
        for (int i = 0; i < 3; i++) {
            WrapService wrapService = (WrapService) context.getBean(WrapService.class);
            System.out.println(wrapService);
            wrapService.say();
        }
    }
}


