package com.github.tonyyan.validator;

import java.lang.reflect.Field;
import java.lang.reflect.Parameter;

/**
 * Created by yanzhichao on 14/11/2017.
 */
public class CheckResponse {

    public CheckResponse(boolean isFailure) {
        this.isFailure = isFailure;
    }

    public CheckResponse(String msg) {
        this.isFailure = true;
        this.msg = msg;
    }

    public CheckResponse(String msg, Field field) {
        this.isFailure = true;
        this.msg = msg;
        this.field = field;
    }

    public CheckResponse(String msg, Parameter parameter) {
        this.isFailure = true;
        this.msg = msg;
        this.parameter = parameter;
    }

    private String msg;
    private boolean isFailure;
    private Field field;
    private Parameter parameter;

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public boolean isFailure() {
        return isFailure;
    }

    public void setFailure(boolean failure) {
        isFailure = failure;
    }

    public Field getField() {
        return field;
    }

    public void setField(Field field) {
        this.field = field;
    }

    @Override
    public String toString() {
        return "CheckResponse{" +
                "msg='" + msg + '\'' +
                ", isFailure=" + isFailure +
                ", field=" + field +
                '}';
    }
}
