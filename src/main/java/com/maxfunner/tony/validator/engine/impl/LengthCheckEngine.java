package com.maxfunner.tony.validator.engine.impl;


import com.maxfunner.tony.validator.CheckResponse;
import com.maxfunner.tony.validator.engine.CheckEngine;
import com.maxfunner.tony.validator.stereotype.LengthCheck;
import com.maxfunner.tony.validator.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.Map;

/**
 * Created by yanzhichao on 15/11/2017.
 */
public class LengthCheckEngine implements CheckEngine<LengthCheck> {


    @Override
    public CheckResponse check(Object value, LengthCheck checkAnnotaion, Field field) {
        return this.check(value, checkAnnotaion, new CheckResponse(getMessage(value, checkAnnotaion, field, null), field));
    }

    @Override
    public CheckResponse check(Object value, LengthCheck checkAnnotaion, Parameter parameter) {
        return this.check(value, checkAnnotaion, new CheckResponse(getMessage(value, checkAnnotaion, null, parameter), parameter));
    }

    @Override
    public CheckResponse check(Object value, LengthCheck checkAnnotaion, CheckResponse failResponse) {
        CheckResponse successResponse = new CheckResponse(false);

        if (checkAnnotaion == null) {
            return successResponse;
        }
        if (value == null) {
            return successResponse;
        }
        long max = checkAnnotaion.max();
        long min = checkAnnotaion.min();
        if (value instanceof String) {
            String valStr = (String) value;
            if (max >= 0 && valStr.length() > max) {
                return failResponse;
            }
            if (min >= 0 && valStr.length() < min) {
                return failResponse;
            }
        }

        if (value instanceof Collection) {
            Collection valCollection = (Collection) value;
            if (max >= 0 && valCollection.size() > max) {
                return failResponse;
            }
            if (min >= 0 && valCollection.size() < min) {
                return failResponse;
            }
        }

        if (value instanceof Map) {
            Map valMap = (Map) value;
            if (max >= 0 && valMap.size() > max) {
                return failResponse;
            }
            if (min >= 0 && valMap.size() < min) {
                return failResponse;
            }
        }

        return successResponse;
    }


    public String getMessage(Object value, LengthCheck lengthCheck, Field field, Parameter parameter) {
        String flagName = "个数";
        if (value instanceof String) {
            flagName = "长度";
        }
        String fieldName = lengthCheck.value();
        if (StringUtils.isEmpty(fieldName)) {
            if (field != null) {
                fieldName = field.getName();
            } else if (parameter != null) {
                fieldName = parameter.getName();
            }
        }
        if (StringUtils.isEmpty(lengthCheck.message())) {
            if (lengthCheck.min() >= 0 && lengthCheck.max() >= 0) {
                if (lengthCheck.max() == lengthCheck.min()) {
                    return fieldName + flagName + "必须为" + lengthCheck.min();
                }
                return fieldName + flagName + "必须在" + lengthCheck.min() + "~" + lengthCheck.max() + "之间";
            }
            if (lengthCheck.min() >= 0) {
                return fieldName + flagName + "必须大于等于" + lengthCheck.min();
            }
            if (lengthCheck.max() >= 0) {
                return fieldName + flagName + "必须小于等于" + lengthCheck.max();
            }
        }
        return lengthCheck.message();
    }

    @Override
    public boolean isSupport(Annotation annotation) {
        return annotation instanceof LengthCheck;
    }
}
