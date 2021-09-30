package com.myspring.framework.factory;

import com.myspring.framework.bean.BeanDefinition;

import java.util.Set;

public interface BeanFactory {

    Object getBean(Class clazz) throws Exception;

    Object getBean(String beanName) throws Exception;

    void registerBeanDefinition(Set<BeanDefinition> beanDefinitions);

    void registerSingleBeanDefinition(BeanDefinition beanDefinition);
}
