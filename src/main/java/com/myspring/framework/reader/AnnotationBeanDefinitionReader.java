package com.myspring.framework.reader;

import com.myspring.framework.annotation.Component;
import com.myspring.framework.annotation.Value;
import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.bean.PropertyValue;

import java.lang.reflect.Field;
import java.util.*;

public class AnnotationBeanDefinitionReader extends AbstractBeanDefinitionReader {
    @Override
    public void loadBeanDefinitions(String path) throws Exception {
        // 1.获取所有类
        Set<Class> classes = getClasses(path);
        Set<BeanDefinition> beanDefinitions = new HashSet<>();

        // 2.找到添加了注解的类
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class clazz = iterator.next();
            Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
            if (componentAnnotation != null) {
                String beanName = componentAnnotation.value();
                String className = clazz.getSimpleName();
                if ("".equals(beanName)) {
                    beanName = className.substring(0,1).toLowerCase()+className.substring(1);
                }
                List<PropertyValue> propertyValues = new ArrayList<>();
                Field[] declaredFields = clazz.getDeclaredFields();
                for (int i = 0; i < declaredFields.length; i++) {
                    Value valueAnnotation = declaredFields[i].getAnnotation(Value.class);
                    if ( valueAnnotation!= null) {
                        valueAnnotation.value();
                        propertyValues.add(new PropertyValue(declaredFields[i].getName(),valueAnnotation.value()));
                    }
                }

                // 3.将这些类添加到原材料集合中
                BeanDefinition beanDefinition = new BeanDefinition(clazz, beanName, propertyValues);
                beanDefinitions.add(beanDefinition);
            }
        }

        setBeanDefinitions(beanDefinitions);
    }

    public Set<Class>  getClasses(String path) {
        return null;
    }
}
