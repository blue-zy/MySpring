package com.myspring.framework.reader;

import com.myspring.framework.annotation.*;
import com.myspring.framework.bean.BeanDefinition;
import com.myspring.framework.bean.BeanReference;
import com.myspring.framework.bean.PropertyValue;
import org.w3c.dom.*;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.net.URL;
import java.net.URLDecoder;
import java.security.InvalidParameterException;
import java.util.*;

public class XmlBeanDefinitionReader extends AbstractBeanDefinitionReader {

    @Override
    public void loadBeanDefinitions(String path) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        path = getClass().getResource("/") + path;
        Document document = db.parse(path);
        NodeList beanNodes = document.getElementsByTagName("bean");
        if (beanNodes.getLength()!=0) {
            setBeanDefinitions(processBeanNodes(beanNodes));
            return;
        }
        NodeList annotationNodes = document.getElementsByTagName("annotation-scan");
        if (annotationNodes.getLength() !=0) {
            setBeanDefinitions(processAnnotation(annotationNodes.item(0)));
        }

    }

    public Set<BeanDefinition> processBeanNodes(NodeList beanNodes) {

        Set<BeanDefinition> beanDefinitions = new HashSet<>();

        // ้ๅbean่็น
        for (int i = 0; i < beanNodes.getLength(); i++) {
            BeanDefinition beanDefinition = new BeanDefinition();
            Boolean isSingleton = true;
            Class clazz = null;
            String beanName = null;
            List<PropertyValue> propertyValues = new ArrayList<>();
            Node beanNode = beanNodes.item(i);
            NamedNodeMap beanNodeAttributes = beanNode.getAttributes();
            for (int j = 0; j < beanNodeAttributes.getLength(); j++) {
                // ่ทๅbean่็น็tagๅvalue
                String tagName = beanNodeAttributes.item(j).getNodeName();
                String tagValue = beanNodeAttributes.item(j).getNodeValue();
                if (tagName.equals("id")) {
                    beanName = tagValue;
                }
                if (tagName.equals("class")) {
                    try {
                        clazz = Class.forName(tagValue);
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                if (tagName.equals("scope")) {
                    //ๅไพ็ๅค็
                    if (tagValue.equals("prototype")) {
                        isSingleton = false;
                    }
                }
            }

            // ่ทๅbean่็นๅ้จ่็น
            NodeList childNodes = beanNode.getChildNodes();

            // get property
            for (int j = 0; j < childNodes.getLength(); j++) {
                Node childNode = childNodes.item(j);
                if (childNode.getNodeType() == Node.ELEMENT_NODE && childNode.getNodeName().equals("property")) {
                    PropertyValue propertyValue = obtainPropertyValue(childNode);
                    propertyValues.add(propertyValue);
                }

            }

            // ๆทปๅ?่ฟๅฅsetไธญ
            beanDefinition.setBeanName(beanName);
            beanDefinition.setClazz(clazz);
            beanDefinition.setPropertyValues(propertyValues);
            beanDefinition.setSingleton(isSingleton);
            beanDefinitions.add(beanDefinition);
        }

        return beanDefinitions;
    }

    public PropertyValue obtainPropertyValue(Node propertyNode) {
        String name = null;
        String value = null;
        String ref = null;

        NamedNodeMap attributes = propertyNode.getAttributes();
        for (int i = 0; i < attributes.getLength(); i++) {
            if (attributes.item(i).getNodeName().equals("name")) {
                name = attributes.item(i).getNodeValue();
            }
            if (attributes.item(i).getNodeName().equals("value")) {
                value = attributes.item(i).getNodeValue();
            }
            if (attributes.item(i).getNodeName().equals("ref")) {
                ref = attributes.item(i).getNodeValue();
            }
        }

        if (value != null && value.length() > 0) {
            return new PropertyValue(name, value);
        }
        if (ref != null && ref.length() > 0) {
            // ๅฐๅผ็จไฟๅญๅฐbeanReferenceไธญๅไธ่ฎพ็ฝฎๅผ็จ็Object
            BeanReference beanReference = new BeanReference(ref);
            return new PropertyValue(name, beanReference);
        }

        return null;
    }

    public Set<BeanDefinition> processAnnotation(Node annotationNode) {
        NamedNodeMap annotationNodeAttributes = annotationNode.getAttributes();
        String path = null;
        if (annotationNodeAttributes.item(0).getNodeName().equals("package")) {
            path = annotationNodeAttributes.item(0).getNodeValue();
        } else {
            throw new InvalidParameterException("่ฏท่ฎพ็ฝฎxmlๆไปถไธญ็packageๅฑๆง๏ผ");
        }
        // 1.่ทๅๆๆ็ฑป
        Set<Class> classes = getClasses(path);
        Set<BeanDefinition> beanDefinitions = new HashSet<>();

        // 2.ๆพๅฐๆทปๅ?ไบๆณจ่งฃ็็ฑป
        Iterator<Class> iterator = classes.iterator();
        while (iterator.hasNext()) {
            Class clazz = iterator.next();

            Component componentAnnotation = (Component) clazz.getAnnotation(Component.class);
            Scope scopeAnnotation = (Scope) clazz.getAnnotation(Scope.class);
            boolean isSingleton = true;
            if (scopeAnnotation != null && scopeAnnotation.value().equals("prototype")) {
                isSingleton = false;
            }
            if (componentAnnotation != null) {
                String beanName = componentAnnotation.value();

                String className = clazz.getSimpleName();
                if ("".equals(beanName)) {
                    beanName = className.substring(0, 1).toLowerCase() + className.substring(1);
                }

                // 3.ๅฐ่ฟไบ็ฑปๆทปๅ?ๅฐๅๆๆ้ๅไธญ
                BeanDefinition beanDefinition = new BeanDefinition();
                processAnnotationProperties(clazz, beanDefinition);
                beanDefinition.setBeanName(beanName);
                beanDefinition.setClazz(clazz);
                beanDefinition.setSingleton(isSingleton);
                beanDefinitions.add(beanDefinition);
            }
        }


        return beanDefinitions;
    }

    public static void processAnnotationProperties(Class clazz, BeanDefinition beanDefinition) {
        List<PropertyValue> propertyValues = new ArrayList<>();
        Field[] declaredFields = clazz.getDeclaredFields();
        for (int i = 0; i < declaredFields.length; i++) {

            Value valueAnnotation = declaredFields[i].getAnnotation(Value.class);
            if (valueAnnotation != null) {
                propertyValues.add(new PropertyValue(declaredFields[i].getName(), valueAnnotation.value()));
            }
            Autowired autowiredAnnotation = declaredFields[i].getAnnotation(Autowired.class);
            if (autowiredAnnotation != null) {
                String name = declaredFields[i].getName();
                Qualifier qualifierAnnotation = declaredFields[i].getAnnotation(Qualifier.class);
                if (qualifierAnnotation != null) {
                    name = qualifierAnnotation.value();
                }
                BeanReference beanReference = new BeanReference(name);
                propertyValues.add(new PropertyValue(declaredFields[i].getName(), beanReference));
            }
        }
        beanDefinition.setPropertyValues(propertyValues);
    }

    public Set<Class> getClasses(String packageName) {
        Set<Class> classes = new HashSet<>();
        String path = packageName.replace('.', '/');
        try {
            Enumeration<URL> resources = Thread.currentThread().getContextClassLoader().getResources(path);
            while (resources.hasMoreElements()) {
                URL url = resources.nextElement();
                System.out.println(url);
                String protocol = url.getProtocol();
                if ("file".equals(protocol)) {
                    // ่ทๅๅ็็ฉ็่ทฏๅพ
                    String filePath = URLDecoder.decode(url.getFile(), "UTF-8");
                    // ไปๆไปถไธญ่ทๅ
                    addClassesByFile(packageName, filePath,classes);
                }
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return classes;
    }

    private void addClassesByFile(String packageName, String path, Set<Class> classes) {
        File dir = new File(path);
        if (!dir.exists() || !dir.isDirectory()) {
            return;
        }
        File[] dirfiles = dir.listFiles();
        for (File file : dirfiles) {
            // ๅฆๆๆฏ็ฎๅฝ ๅ้ๅฝๆซๆ
            if (file.isDirectory()) {
                addClassesByFile(packageName + "." + file.getName(), file.getAbsolutePath(), classes);
            } else {
                // ๅฆๆๆฏjava็ฑปๆไปถ ๅปๆๅ้ข็.class ๅช็ไธ็ฑปๅ
                String className = file.getName().substring(0, file.getName().length() - 6);
                try {
                    classes.add(Class.forName(packageName + '.' + className));
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
        }

    }

}