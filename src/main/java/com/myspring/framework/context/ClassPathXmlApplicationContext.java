package com.myspring.framework.context;

import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.factory.AbstractBeanFactory;
import com.myspring.framework.factory.AutowiredBeanFactory;
import com.myspring.framework.factory.BeanFactory;
import com.myspring.framework.reader.AbstractBeanDefinitionReader;
import com.myspring.framework.reader.XmlBeanDefinitionReader;

import java.util.Iterator;
import java.util.Set;

public class ClassPathXmlApplicationContext extends AbstractApplicationContext {

    private String path;

    public ClassPathXmlApplicationContext(String path) {
        this.path = path;
        refresh();
    }

    public void refresh() {
        synchronized (this) {
            AbstractBeanFactory beanFactory = obtainBeanFactory();
            prepareBeanFactory(beanFactory);
            setBeanFactory(beanFactory);
        }
    }

    public void prepareBeanFactory(AbstractBeanFactory beanFactory) {
        beanFactory.populateBeans();
    }

    public AbstractBeanFactory obtainBeanFactory() {
        AbstractBeanDefinitionReader beanDefinitionReader = new XmlBeanDefinitionReader();
        Set<BeanDefinition> beanDefinitions = beanDefinitionReader.getBeanDefinitions(path);
        AbstractBeanFactory beanFactory = new AutowiredBeanFactory();
        beanFactory.registerBeanDefinition(beanDefinitions);
        return beanFactory;
    }

    public void addNewBeanDefinition(BeanDefinition beanDefinition) {
        XmlBeanDefinitionReader.processAnnotationProperties(beanDefinition.getClazz(), beanDefinition);
        getBeanFactory().registerSingleBeanDefinition(beanDefinition);
    }

    public void refreshBeanFactory() {
        prepareBeanFactory((AbstractBeanFactory) getBeanFactory());
    }
}
