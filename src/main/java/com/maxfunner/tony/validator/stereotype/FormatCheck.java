package com.maxfunner.tony.validator.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yanzhichao on 14/11/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface FormatCheck {

    int EMAIL = 1;
    int PHONE = 2;
    int IPADDR = 3;
    int URL = 4;
    int IDDENTITY_CARD = 5;
    int CHINESE_CHAR = 6;

    int formatType();

    String message() default "";

    String value() default "";

}
