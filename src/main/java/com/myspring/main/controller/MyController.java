package com.myspring.main.controller;

import com.myspring.framework.annotation.Autowired;
import com.myspring.framework.annotation.Controller;
import com.myspring.framework.annotation.RequestMapping;
import com.myspring.framework.annotation.RequestParam;
import com.myspring.main.service.HelloService;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Controller
@RequestMapping("/hello")
public class MyController {

    @Autowired
    private HelloService helloService;

    @RequestMapping("/test")
    public void testMapping(HttpServletRequest request, HttpServletResponse response, @RequestParam("param1") String para) {
        try {
            response.getWriter().write("hello");
            System.out.println(para);
            helloService.sayHello();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
