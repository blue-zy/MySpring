package com.myspring.framework.context;

import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.factory.BeanFactory;

public abstract class AbstractApplicationContext implements ApplicationContext {

    private BeanFactory beanFactory;

    @Override
    public Object getBean(Class clazz) throws Exception {
        return beanFactory.getBean(clazz);
    }

    @Override
    public Object getBean(String beanName) throws Exception {
        return beanFactory.getBean(beanName);
    }

    public BeanFactory getBeanFactory() {
        return beanFactory;
    }

    public void setBeanFactory(BeanFactory beanFactory) {
        this.beanFactory = beanFactory;
    }

    public abstract void addNewBeanDefinition(BeanDefinition beanDefinition);

    public abstract void refreshBeanFactory();
}
