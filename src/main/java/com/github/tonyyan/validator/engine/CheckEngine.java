package com.github.tonyyan.validator.engine;

import com.github.tonyyan.validator.CheckResponse;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * Created by yanzhichao on 14/11/2017.
 */
public interface CheckEngine<T>  {

    CheckResponse check(Object value, T checkAnnotaion, Field field);

    CheckResponse check(Object value, T checkAnnotaion, Parameter parameter);

    CheckResponse check(Object value, T checkAnnotaion, CheckResponse failResponse);

    boolean isSupport(Annotation annotation);

}
