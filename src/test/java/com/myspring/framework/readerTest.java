package com.myspring.framework;

import com.myspring.framework.reader.XmlBeanDefinitionReader;
import org.junit.Test;

import java.lang.annotation.Target;

public class readerTest {

    @Test
    public void xmlReaderTest() {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader();
        String path = "application.xml";
        try {
            xmlBeanDefinitionReader.loadBeanDefinitions(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    public void annotationReaderTest() {
        XmlBeanDefinitionReader xmlBeanDefinitionReader = new XmlBeanDefinitionReader();
        String path = "com.myspring.main";
        xmlBeanDefinitionReader.getClasses(path);

    }
}
