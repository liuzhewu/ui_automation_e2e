package com.liu.listener;

import com.liu.util.CustomRetryAnalyzer;
import org.testng.IAnnotationTransformer;
import org.testng.IRetryAnalyzer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;

public class CustomRetryListener implements IAnnotationTransformer {

    @Override
    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {
        IRetryAnalyzer iRetryAnalyzer = annotation.getRetryAnalyzer();
        if (null == iRetryAnalyzer) {
            annotation.setRetryAnalyzer(CustomRetryAnalyzer.class);
        }


    }
}
