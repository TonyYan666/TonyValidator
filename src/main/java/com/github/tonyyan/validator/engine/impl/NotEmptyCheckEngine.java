package com.github.tonyyan.validator.engine.impl;

import com.github.tonyyan.validator.CheckResponse;
import com.github.tonyyan.validator.engine.CheckEngine;
import com.github.tonyyan.validator.stereotype.NotEmptyCheck;
import com.github.tonyyan.validator.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yanzhichao on 14/11/2017.
 */
public class NotEmptyCheckEngine implements CheckEngine<NotEmptyCheck> {


    @Override
    public CheckResponse check(Object value, NotEmptyCheck checkAnnotaion, Field field) {
        return this.check(value, checkAnnotaion, new CheckResponse(getCheckMessage(checkAnnotaion, field, null), field));
    }

    @Override
    public CheckResponse check(Object value, NotEmptyCheck checkAnnotaion, Parameter parameter) {
        return this.check(value, checkAnnotaion, new CheckResponse(getCheckMessage(checkAnnotaion, null, parameter), parameter));
    }

    @Override
    public CheckResponse check(Object value, NotEmptyCheck checkAnnotaion, CheckResponse failResponse) {
        CheckResponse successResponse = new CheckResponse(false);

        if (checkAnnotaion == null) {
            return successResponse;
        }

        if (value == null) {
            return failResponse;
        }

        if (value instanceof String) {
            String valueStr = (String) value;
            if (StringUtils.isEmpty(valueStr)) {
                return failResponse;
            }
        }
        if (value instanceof Collection) {
            Collection collection = (Collection) value;
            if (collection.isEmpty() || collection.size() <= 0) {
                return failResponse;
            }
        }
        if (value instanceof Map) {
            Map map = (Map) value;
            if (map.isEmpty() || map.size() <= 0) {
                return failResponse;
            }
        }
        return new CheckResponse(false);
    }

    @Override
    public boolean isSupport(Annotation annotation) {
        return annotation instanceof NotEmptyCheck;
    }

    public String getCheckMessage(NotEmptyCheck notEmptyCheck, Field field, Parameter parameter) {
        String fieldName = notEmptyCheck.value();
        if (StringUtils.isEmpty(fieldName)) {
            if (field != null) {
                fieldName = field.getName();
            } else if (parameter != null) {
                fieldName = parameter.getName();
            }
        }
        if (notEmptyCheck.message().isEmpty()) {
            return fieldName + " 不能为空";
        }
        return notEmptyCheck.message();
    }
}
