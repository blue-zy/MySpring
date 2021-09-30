package com.myspring.framework.reader;

import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.factory.BeanFactory;

import java.util.HashSet;
import java.util.Set;

public abstract class AbstractBeanDefinitionReader implements BeanDefinitionReader {

    private Set<BeanDefinition> beanDefinitions;


    public void setBeanDefinitions(Set<BeanDefinition> beanDefinitions) {
        this.beanDefinitions = beanDefinitions;
    }


    public Set<BeanDefinition> getBeanDefinitions(String path) {
        try {
            loadBeanDefinitions(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return beanDefinitions;
    }

}
