package com.myspring.framework.bean;

import java.util.List;

public class BeanDefinition {

    private Class clazz;

    private String beanName;

    private Boolean singleton;

    public List<PropertyValue> getPropertyValues() {
        return propertyValues;
    }

    public BeanDefinition() {

    }

    public BeanDefinition(Class clazz, String beanName, List<PropertyValue> propertyValues) {
        this.clazz = clazz;
        this.beanName = beanName;
        this.propertyValues = propertyValues;
    }

    public void setPropertyValues(List<PropertyValue> propertyValues) {
        this.propertyValues = propertyValues;
    }

    private List<PropertyValue> propertyValues;


    public Class getClazz() {
        return clazz;
    }

    public void setClazz(Class clazz) {
        this.clazz = clazz;
    }

    public String getBeanName() {
        return beanName;
    }

    public void setBeanName(String beanName) {
        this.beanName = beanName;
    }

    public Boolean isSingleton() {
        return singleton;
    }

    public void setSingleton(Boolean singleton) {
        this.singleton = singleton;
    }
}
