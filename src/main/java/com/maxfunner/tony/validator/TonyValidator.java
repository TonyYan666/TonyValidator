package com.maxfunner.tony.validator;

import com.maxfunner.tony.validator.engine.CheckEngine;
import com.maxfunner.tony.validator.engine.impl.FigureCheckEngine;
import com.maxfunner.tony.validator.engine.impl.FormatCheckEngine;
import com.maxfunner.tony.validator.engine.impl.LengthCheckEngine;
import com.maxfunner.tony.validator.engine.impl.NotEmptyCheckEngine;
import com.maxfunner.tony.validator.stereotype.IgnoreCheck;
import com.maxfunner.tony.validator.stereotype.NestedObjectCheck;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.*;

/**
 * Created by yanzhichao on 14/11/2017.
 */
@Aspect
public class TonyValidator {

    private ValidatorCallBack validatorCallBack;

    private List<CheckEngine> checkEngines = null;

    public TonyValidator(ValidatorCallBack validatorCallBack) {
        initEngineList();
        this.validatorCallBack = validatorCallBack;
    }

    public TonyValidator() {
        initEngineList();
    }

    //实例化所支持的检查引擎
    private void initEngineList(){
        this.checkEngines = new ArrayList<>();
        this.checkEngines.add(new NotEmptyCheckEngine());
        this.checkEngines.add(new FigureCheckEngine());
        this.checkEngines.add(new LengthCheckEngine());
        this.checkEngines.add(new FormatCheckEngine());
    }


    public ValidatorCallBack getValidatorCallBack() {
        return validatorCallBack;
    }

    public void setValidatorCallBack(ValidatorCallBack validatorCallBack) {
        this.validatorCallBack = validatorCallBack;
    }


    @Around("@annotation(com.maxfunner.tony.validator.stereotype.ParamVerification)")
    public Object checkParam(ProceedingJoinPoint joinPoint) throws Throwable {
        CheckResponse checkResponse = null;
        Set<Integer> ignoreSet = new HashSet<>();
        Method method = getMethodFromJoinPoin(joinPoint);

        //对参数本身进行检查
        Parameter[] parameters = method.getParameters();
        for (int i = 0; i < parameters.length; i++) {
            Annotation[] annotationArray = parameters[i].getAnnotations();
            for (Annotation annotation : annotationArray) {
                if (annotation instanceof IgnoreCheck) {
                    ignoreSet.add(i);
                }
                //遍历校验引擎，进行参数字段校验
                Object value = joinPoint.getArgs()[i];
                for (CheckEngine checkEngine : this.checkEngines) {
                    if (checkEngine.isSupport(annotation)) {
                        checkResponse = checkEngine.check(value, annotation, parameters[i]);
                        if (checkResponse.isFailure()) {
                            return validatorCallBack.checkFailedResponse(checkResponse);
                        }
                    }
                }
            }

        }

        //校验参数体内的字段
        int counter = 0;
        for (Object paramObj : joinPoint.getArgs()) {

            //排除打上忽略标签的参数
            if (ignoreSet.contains(counter)) {
                continue;
            }

            //如果参数体本来是List 或 Set 那就检查里面的对象体内的字段
            if (paramObj instanceof Collection) {
                Collection collection = (Collection) paramObj;
                for (Object collectionItem : collection) {
                    checkResponse = checkParamObj(collectionItem);
                    if (checkResponse.isFailure()) {
                        return validatorCallBack.checkFailedResponse(checkResponse);
                    }
                }
            } else {
                checkResponse = checkParamObj(paramObj);
                if (checkResponse.isFailure()) {
                    return validatorCallBack.checkFailedResponse(checkResponse);
                }
            }
            counter++;
        }

        return joinPoint.proceed(joinPoint.getArgs());
    }


    //通过JoinPoint 获得方法体
    private Method getMethodFromJoinPoin(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        MethodSignature methodSignature = (MethodSignature) signature;
        Method targetMethod = methodSignature.getMethod();
        return targetMethod;
    }


    //检查每个需要检验的对象体字段
    private CheckResponse checkParamObj(Object object) throws IllegalAccessException {
        CheckResponse checkResponse = new CheckResponse(false);
        //如果为空之前返回成功，如果打上NotEmpty，上面的参数校验就会马上拦截 不会进入到对象体字段校验
        if (object == null) {
            return checkResponse;
        }

        //遍历对象体字段
        Class targetType = object.getClass();
        Field[] fieldArray = targetType.getDeclaredFields();
        for (Field field : fieldArray) {
            checkResponse = checkField(field, object);
            if (checkResponse.isFailure()) {
                return checkResponse;
            }
        }
        return checkResponse;
    }

    //通过定义好的校验引擎逐个校验其字段的内容
    private CheckResponse checkField(Field field, Object object) throws IllegalAccessException {
        Annotation[] fieldAnnotations = field.getAnnotations();
        field.setAccessible(true);
        Object value = field.get(object);
        CheckResponse checkResponse = new CheckResponse(false);

        //是否需要嵌套遍历对象体
        boolean hasNestedObjectCheck = false;
        for (Annotation annotation : fieldAnnotations) {
            //检查字段是否需要检查内嵌对象体字段
            if (annotation instanceof NestedObjectCheck) {
                hasNestedObjectCheck = true;
            }
            //通过校验引擎遍历校验字段
            for (CheckEngine checkEngine : this.checkEngines) {
                if (checkEngine.isSupport(annotation)) {
                    checkResponse = checkEngine.check(value, annotation, field);
                    if (checkResponse.isFailure()) {
                        return checkResponse;
                    }
                }
            }
        }

        //如果不为空，且需要嵌套校验其内部字段，即往下继续嵌套校验
        if (hasNestedObjectCheck && value != null) {
            if (value instanceof Collection) {
                Collection collection = (Collection) value;
                for (Object item : collection) {
                    CheckResponse itemCheckResponse = checkParamObj(item);
                    if (itemCheckResponse.isFailure()) {
                        return itemCheckResponse;
                    }
                }
            } else {
                return checkParamObj(value);
            }
        }
        return checkResponse;
    }


}
