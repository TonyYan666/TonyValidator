package com.maxfunner.tony.validator.engine.impl;

import com.maxfunner.tony.validator.CheckResponse;
import com.maxfunner.tony.validator.engine.CheckEngine;
import com.maxfunner.tony.validator.stereotype.FigureCheck;
import com.maxfunner.tony.validator.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.math.BigDecimal;

/**
 * Created by yanzhichao on 15/11/2017.
 */
public class FigureCheckEngine implements CheckEngine<FigureCheck> {


    @Override
    public CheckResponse check(Object value, FigureCheck checkAnnotaion, Field field) {
        return check(value, checkAnnotaion, new CheckResponse(getMessage(checkAnnotaion, field, null), field));
    }

    @Override
    public CheckResponse check(Object value, FigureCheck checkAnnotaion, Parameter parameter) {
        return check(value, checkAnnotaion, new CheckResponse(getMessage(checkAnnotaion, null, parameter), parameter));
    }

    @Override
    public CheckResponse check(Object value, FigureCheck checkAnnotaion, CheckResponse failResponse) {
        CheckResponse successResponse = new CheckResponse(false);

        if (checkAnnotaion == null) {
            return successResponse;
        }
        if (value == null) {
            return successResponse;
        }
        long max = checkAnnotaion.max();
        long min = checkAnnotaion.min();
        if (value instanceof BigDecimal) {
            BigDecimal bigDecimal = (BigDecimal) value;
            if (min >= 0 && bigDecimal.compareTo(new BigDecimal(min)) < 0) {
                return failResponse;
            }
            if (max >= 0 && bigDecimal.compareTo(new BigDecimal(max)) > 0) {
                return failResponse;
            }
        }

        if (value instanceof Integer || value instanceof Double ||
                value instanceof Long || value instanceof Short ||
                value instanceof Float) {
            BigDecimal bigDecimal = new BigDecimal(String.valueOf(value));
            if (min > 0 && bigDecimal.compareTo(new BigDecimal(min)) < 0) {
                return failResponse;
            }
            if (max >= 0 && bigDecimal.compareTo(new BigDecimal(max)) > 0) {
                return failResponse;
            }
        }


        return successResponse;
    }

    public String getMessage(FigureCheck figureCheck, Field field, Parameter parameter) {
        String fieldName = figureCheck.value();

        if (StringUtils.isEmpty(fieldName)) {
            if (field != null) {
                fieldName = field.getName();
            } else if (parameter != null) {
                fieldName = parameter.getName();
            }
        }

        if (StringUtils.isEmpty(figureCheck.message())) {
            if (figureCheck.min() >= 0 && figureCheck.max() >= 0) {
                return fieldName + "必须在" + figureCheck.min() + "~" + figureCheck.max() + "之间";
            }
            if (figureCheck.min() >= 0) {
                return fieldName + "必须大于等于" + figureCheck.min();
            }
            if (figureCheck.max() >= 0) {
                return fieldName + "必须小于等于" + figureCheck.max();
            }
        }
        return figureCheck.message();
    }


    @Override
    public boolean isSupport(Annotation annotation) {
        return annotation instanceof FigureCheck;
    }

}
