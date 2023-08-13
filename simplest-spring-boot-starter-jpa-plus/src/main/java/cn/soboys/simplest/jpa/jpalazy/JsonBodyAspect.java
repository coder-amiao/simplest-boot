package cn.soboys.simplest.jpa.jpalazy;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.lang.reflect.Method;
import java.util.Collection;


/**
 * @version: 2.2
 * @author: disco
 * @description: Json Response 切面
 * @date: 2019/11/8
 */
@Component
@Aspect
@Slf4j
@Order(-1)
public class JsonBodyAspect {

    /**
     * 配置切入点
     */

    @Pointcut("@annotation(cn.soboys.simplest.jpa.jpalazy.JsonBody)")
    public void jsonBodyPointcut() {
        // 该方法无方法体,主要为了让同类中其他方法使用此切入点
    }

    /**
     * 配置环绕通知,使用在方法jsonBodyPointcut()上注册的切入点
     *
     * @param joinPoint join point for advice
     */
    @Around("jsonBodyPointcut()")
    public Object jsonBodyAround(ProceedingJoinPoint joinPoint) throws Throwable {
        //获取返回方法返回结果
        Object object = joinPoint.proceed();
        boolean isCollection = (object instanceof Collection);
        //定位拦截的具体方法
        Class<?> targetCls = joinPoint.getTarget().getClass();
        MethodSignature ms = (MethodSignature) joinPoint.getSignature();
        Method targetMethod = targetCls.getDeclaredMethod(ms.getName(), ms.getParameterTypes());

        if (targetMethod.isAnnotationPresent(JsonBody.class)) {
            //获取JsonBody注解
            JsonBody jsonBody = targetMethod.getAnnotation(JsonBody.class);
            JsonBodyUtil.storeJsonBody(jsonBody, targetMethod.toGenericString());
        }

        return object;
    }

}

