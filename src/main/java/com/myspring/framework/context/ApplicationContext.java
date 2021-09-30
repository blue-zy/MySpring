package com.myspring.framework.context;

public interface ApplicationContext {

    Object getBean(Class clazz) throws Exception;

    Object getBean(String beanName) throws Exception;
}
