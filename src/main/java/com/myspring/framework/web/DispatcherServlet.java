package com.myspring.framework.web;

import com.myspring.framework.annotation.Controller;
import com.myspring.framework.annotation.RequestMapping;
import com.myspring.framework.annotation.RequestParam;
import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.context.AbstractApplicationContext;
import com.myspring.framework.context.ClassPathXmlApplicationContext;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.*;


@WebServlet(name = "index", urlPatterns ="/*", loadOnStartup = 1)
public class DispatcherServlet extends HttpServlet {

    private AbstractApplicationContext applicationContext;

    private Properties properties;

    public Set<Class> classes;

    public Set<Class> mvcClasses;

    private Map<String, Method> handlerMapping = new HashMap<>();

    private Map<String, Object> controllerMap = new HashMap<>();

    @Override
    public void init() throws ServletException {
        applicationContext = new ClassPathXmlApplicationContext("application-annotation.xml");
        properties = new Properties();
        classes = new HashSet<>();
        doLoadConfig("application.properties");
        doScanner(properties.getProperty("scanPackage"));
        doInstance();
        initHandlerMapping();
    }


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(this.getClass().getName() + " doGet");
        doDispatch(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        System.out.println(this.getClass().getName() + " doPost");
        doDispatch(req, resp);
    }

    private void doDispatch(HttpServletRequest req, HttpServletResponse resp) {
        if (handlerMapping.isEmpty()) {
            return;
        }
        String url = req.getRequestURI();
        String contextPath = req.getContextPath();
        url = url.replace(contextPath, "");
        if (!handlerMapping.containsKey(url)) {
            try {
                resp.getWriter().write("404 not found!");
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }
        Method method = handlerMapping.get(url);
        Class<?>[] parameterTypes = method.getParameterTypes();
        Parameter[] parameters = method.getParameters();
        Object[] paramValues = new Object[parameterTypes.length];
        Map<String, String[]> parameterMap = req.getParameterMap();

        for (int i = 0; i < parameterTypes.length; i++) {
            String requestParam = parameterTypes[i].getSimpleName();
            if (requestParam.equals("HttpServletRequest")) {
                paramValues[i] = req;
                continue;
            }
            if (requestParam.equals("HttpServletResponse")) {
                paramValues[i] = resp;
                continue;
            }
            if (requestParam.equals("String")) {
                if (!parameters[i].isAnnotationPresent(RequestParam.class)) {
                    throw new InvalidParameterException("没有用注解指定参数！");
                }
                String paramName = parameters[i].getAnnotation(RequestParam.class).value();

                for (Map.Entry<String, String[]> param : parameterMap.entrySet()) {
                    if (paramName.equals(param.getKey())) {
                        String value = Arrays.toString(param.getValue()).replaceAll("\\[|\\]", "").replaceAll(",\\s", ",");
                        paramValues[i] = value;
                    }
                }
            }
        }

        try {
            method.invoke(controllerMap.get(url), paramValues);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    private void doLoadConfig(String location) {
        //把web.xml中的contextConfigLocation对应value值的文件加载到流里面
        try (InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream(location);) {
            properties.load(resourceAsStream);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void doScanner(String packageName) {
        // .替换成/
        URL url = this.getClass().getClassLoader().getResource("/" + packageName.replace(".", "/"));
        File dir = new File(url.getFile());
        for (File file : dir.listFiles()) {
            if (file.isDirectory()) {
                // 递归读取包
                doScanner(packageName + "." + file.getName());
            } else {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void doInstance() {
        if (classes.isEmpty()) {
            return;
        }
        Iterator<Class> iterator = classes.iterator();
        mvcClasses = new HashSet<>();
        if (iterator.hasNext()) {
            Class clazz = iterator.next();
            System.out.println(clazz.getName()+" do instance");
            if (clazz.isAnnotationPresent(Controller.class)) {
                System.out.println(clazz.getName()+" has controller annotation");
                mvcClasses.add(clazz);
                BeanDefinition beanDefinition = new BeanDefinition();
                beanDefinition.setClazz(clazz);
                beanDefinition.setSingleton(true);
                beanDefinition.setBeanName(clazz.getSimpleName().substring(0, 1).toLowerCase() + clazz.getSimpleName().substring(1));
                applicationContext.addNewBeanDefinition(beanDefinition);
                applicationContext.refreshBeanFactory();
            }

        }


    }

    private void initHandlerMapping() {
        if (mvcClasses.isEmpty()) {
            return;
        }
        Iterator<Class> iterator = mvcClasses.iterator();
        while (iterator.hasNext()) {
            Class clazz = iterator.next();
            System.out.println(clazz+" init handler mapping");
            String url = "";
            if (clazz.isAnnotationPresent(RequestMapping.class)) {
                RequestMapping requestMapping = (RequestMapping) clazz.getAnnotation(RequestMapping.class);
                url = requestMapping.value();
            }
            Method[] declaredMethods = clazz.getDeclaredMethods();
            for (Method declaredMethod : declaredMethods) {
                if (declaredMethod.isAnnotationPresent(RequestMapping.class)) {
                    url = url + declaredMethod.getAnnotation(RequestMapping.class).value();
                    handlerMapping.put(url, declaredMethod);
                    System.out.println(clazz+" being added to hanlderMapping ");
                    System.out.println(url);
                    System.out.println(declaredMethod.getName());
                    try {
                        controllerMap.put(url, applicationContext.getBean(clazz));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }
}
