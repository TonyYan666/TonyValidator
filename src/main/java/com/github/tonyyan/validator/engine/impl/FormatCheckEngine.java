package com.github.tonyyan.validator.engine.impl;

import com.github.tonyyan.validator.CheckResponse;
import com.github.tonyyan.validator.engine.CheckEngine;
import com.github.tonyyan.validator.stereotype.FormatCheck;
import com.github.tonyyan.validator.utils.StringUtils;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Parameter;
import java.util.regex.Pattern;

/**
 * Created by yanzhichao on 15/11/2017.
 */
public class FormatCheckEngine implements CheckEngine<FormatCheck> {


    @Override
    public CheckResponse check(Object value, FormatCheck checkAnnotaion, Field field) {
        return check(value, checkAnnotaion, new CheckResponse(getMessage(checkAnnotaion, field, null), field));
    }

    @Override
    public CheckResponse check(Object value, FormatCheck checkAnnotaion, Parameter parameter) {
        return check(value, checkAnnotaion, new CheckResponse(getMessage(checkAnnotaion, null, parameter), parameter));
    }

    @Override
    public CheckResponse check(Object value, FormatCheck checkAnnotaion, CheckResponse failResponse) {
        CheckResponse successResponse = new CheckResponse(false);

        if (value == null) {
            return successResponse;
        }

        if (checkAnnotaion == null) {
            return successResponse;
        }

        if (!(value instanceof String)) {
            return failResponse;
        }

        String valStr = (String) value;

        if (checkAnnotaion.formatType() == FormatCheck.EMAIL) {
            if (!isEmail(valStr)) {
                return failResponse;
            }
        }

        if (checkAnnotaion.formatType() == FormatCheck.PHONE) {
            if (!isMobile(valStr)) {
                return failResponse;
            }
        }

        if (checkAnnotaion.formatType() == FormatCheck.IPADDR) {
            if (!isIPAddr(valStr)) {
                return failResponse;
            }
        }

        if (checkAnnotaion.formatType() == FormatCheck.URL) {
            if (!isUrl(valStr)) {
                return failResponse;
            }
        }

        if (checkAnnotaion.formatType() == FormatCheck.IDDENTITY_CARD) {
            if (!isIDCard(valStr)) {
                return failResponse;
            }
        }

        if (checkAnnotaion.formatType() == FormatCheck.CHINESE_CHAR) {
            if (!isChinese(valStr)) {
                return failResponse;
            }
        }

        return successResponse;
    }


    public String getMessage(FormatCheck formatCheck, Field field, Parameter parameter) {
        String fieldName = formatCheck.value();

        if (StringUtils.isEmpty(fieldName)) {
            if (field != null) {
                fieldName = field.getName();
            } else if (parameter != null) {
                fieldName = parameter.getName();
            }
        }

        if (StringUtils.isEmpty(formatCheck.message())) {
            return "你输入的 " + fieldName + " 格式不正确";
        }
        return formatCheck.message();
    }

    @Override
    public boolean isSupport(Annotation annotation) {
        return annotation instanceof FormatCheck;
    }


    /**
     * 正则表达式：验证手机号
     */
    public static final String REGEX_MOBILE = "^((17[0-9])|(14[0-9])|(13[0-9])|(15[^4,\\D])|(18[0,5-9]))\\d{8}$";

    /**
     * 正则表达式：验证邮箱
     */
    public static final String REGEX_EMAIL = "^([a-z0-9A-Z]+[-|\\.]?)+[a-z0-9A-Z]@([a-z0-9A-Z]+(-[a-z0-9A-Z]+)?\\.)+[a-zA-Z]{2,}$";

    /**
     * 正则表达式：验证汉字
     */
    public static final String REGEX_CHINESE = "^[\u4e00-\u9fa5],{0,}$";

    /**
     * 正则表达式：验证身份证
     */
    public static final String REGEX_ID_CARD = "(^\\d{18}$)|(^\\d{15}$)";

    /**
     * 正则表达式：验证URL
     */
    public static final String REGEX_URL = "http(s)?://([\\w-]+\\.)+[\\w-]+(/[\\w- ./?%&=]*)?";

    /**
     * 正则表达式：验证IP地址
     */
    public static final String REGEX_IP_ADDR = "(25[0-5]|2[0-4]\\d|[0-1]\\d{2}|[1-9]?\\d)";


    /**
     * 校验手机号
     *
     * @param mobile
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isMobile(String mobile) {
        return Pattern.matches(REGEX_MOBILE, mobile);
    }

    /**
     * 校验邮箱
     *
     * @param email
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isEmail(String email) {
        return Pattern.matches(REGEX_EMAIL, email);
    }

    /**
     * 校验汉字
     *
     * @param chinese
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isChinese(String chinese) {
        return Pattern.matches(REGEX_CHINESE, chinese);
    }

    /**
     * 校验身份证
     *
     * @param idCard
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isIDCard(String idCard) {
        return Pattern.matches(REGEX_ID_CARD, idCard);
    }

    /**
     * 校验URL
     *
     * @param url
     * @return 校验通过返回true，否则返回false
     */
    public static boolean isUrl(String url) {
        return Pattern.matches(REGEX_URL, url);
    }

    /**
     * 校验IP地址
     *
     * @param ipAddr
     * @return
     */
    public static boolean isIPAddr(String ipAddr) {
        return Pattern.matches(REGEX_IP_ADDR, ipAddr);
    }


}
