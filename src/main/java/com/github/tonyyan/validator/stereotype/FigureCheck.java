package com.github.tonyyan.validator.stereotype;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by yanzhichao on 14/11/2017.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD,ElementType.PARAMETER})
public @interface FigureCheck {

    long min() default -1;

    long max() default -1;

    String value() default "";

    String message() default "";

}
