package com.myspring.framework.factory;

import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.bean.BeanReference;
import com.myspring.framework.bean.PropertyValue;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

public class AutowiredBeanFactory extends AbstractBeanFactory{

    @Override
    Object doCreateBean(BeanDefinition beanDefinition) {
        Object bean = null;
        try {
            Class beanClazz = beanDefinition.getClazz();
            bean = beanClazz.newInstance();
            if (!beanMap.containsKey(beanDefinition.getBeanName())){
                putIntoBeanMap(beanDefinition.getBeanName(), bean);
            }
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        addPropertyValues(bean, beanDefinition.getPropertyValues());
        return bean;
    }

    void addPropertyValues(Object bean, List<PropertyValue> propertyValues) {
        if (propertyValues != null) {
            for (int i = 0; i < propertyValues.size(); i++) {
                PropertyValue propertyValue = propertyValues.get(i);
                if (propertyValue.getValue() instanceof BeanReference) {
                    try {
                        Field field = bean.getClass().getDeclaredField(propertyValue.getName());
                        Object refBean = populateBeanByBeanReference(((BeanReference) propertyValue.getValue()).getName());
                        ((BeanReference) propertyValue.getValue()).setBean(refBean);
                        if (field != null) {
                            field.setAccessible(true);
                            field.set(bean, ((BeanReference) propertyValue.getValue()).getBean());
                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                } else {
                    try {
                        Field field = bean.getClass().getDeclaredField(propertyValue.getName());
                        if (field != null) {
                            field.setAccessible(true);
                            String typeName = field.getType().getSimpleName();
                            String value = (String) propertyValue.getValue();
                            switch (typeName) {
                                case "Integer":
                                case "int":
                                    field.set(bean, Integer.parseInt(value));
                                    break;
                                case "String":
                                    field.set(bean, value);
                                    break;
                                case "Float":
                                case "float":
                                    field.set(bean, Float.parseFloat(value));
                                    break;
                                case "Double":
                                case "double":
                                    field.set(bean, Double.parseDouble(value));
                                    break;
                                case "Character":
                                case "char":
                                    field.set(bean, value.charAt(0));
                                    break;
                                case "Long":
                                case "long":
                                    field.set(bean,Long.parseLong(value));
                                    break;
                                case "Short":
                                case "short":
                                    field.set(bean,Short.parseShort(value));
                                    break;
                                case "Byte":
                                case "byte":
                                    field.set(bean,Byte.parseByte(value));
                                    break;
                            }


                        }
                    } catch (NoSuchFieldException e) {
                        e.printStackTrace();
                    } catch (IllegalAccessException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

}
