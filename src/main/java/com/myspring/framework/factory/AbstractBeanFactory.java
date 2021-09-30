package com.myspring.framework.factory;

import com.myspring.framework.bean.BeanDefinition;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public abstract class AbstractBeanFactory implements BeanFactory {

    private Set<BeanDefinition> beanDefinitionSet;

    Map<String, Object> beanMap = new ConcurrentHashMap<>();

    @Override
    public Object getBean(Class clazz) throws Exception {
        Object bean = null;
        Iterator<BeanDefinition> iterator = beanDefinitionSet.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            if (beanDefinition.getClazz() == clazz) {
                if (!beanDefinition.isSingleton()) {
                    return doCreateBean(beanDefinition);
                }
                bean = beanMap.get(beanDefinition.getBeanName());
                if (bean != null) {
                    return bean;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        Iterator<BeanDefinition> iterator = beanDefinitionSet.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            if (beanDefinition.getBeanName() == beanName) {
                if (!beanDefinition.isSingleton()) {
                    return doCreateBean(beanDefinition);
                }
            }
        }
        Object bean = beanMap.get(beanName);
        if (bean != null) {
            return bean;
        }
        return null;
    }

    public void registerBeanDefinition(Set<BeanDefinition> beanDefinitions) {
        this.beanDefinitionSet = beanDefinitions;
    }

    public void registerSingleBeanDefinition(BeanDefinition beanDefinition) {
        this.beanDefinitionSet.add(beanDefinition);
    }

    abstract Object doCreateBean(BeanDefinition beanDefinition);

    public Object populateBeanByBeanReference(String beanName) {
        Iterator<BeanDefinition> iterator = beanDefinitionSet.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            if (beanDefinition.getBeanName().equals(beanName)) {
                if (!beanDefinition.isSingleton()) {
                    Object bean = doCreateBean(beanDefinition);
                    return bean;
                } else {
                    if (!beanMap.containsKey(beanName)) {
                        Object bean = doCreateBean(beanDefinition);
                        beanMap.put(beanName, bean);
                        return bean;
                    } else {
                        return beanMap.get(beanName);
                    }
                }
            }
        }
        return null;
    }

    public void populateBeans() {
        Iterator<BeanDefinition> iterator = beanDefinitionSet.iterator();
        while (iterator.hasNext()) {
            BeanDefinition beanDefinition = iterator.next();
            if (beanDefinition.isSingleton()) {
                Object bean = doCreateBean(beanDefinition);
            }
        }
    }

    public void putIntoBeanMap(String beanName, Object bean) {
        beanMap.put(beanName, bean);
    }

}
