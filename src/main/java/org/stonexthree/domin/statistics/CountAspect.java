package org.stonexthree.domin.statistics;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.stonexthree.domin.Notification;
import org.stonexthree.domin.UseNotification;

import java.lang.reflect.Method;

/**
 * 统计切面，当标记 @ViewAdd 的方法执行时，相关文档对浏览量增加
 */
@Aspect
@Component
@Slf4j
public class CountAspect {
    private DocCounter docCounter;
    private CountDataHolder countDataHolder;

    public CountAspect(StatisticsService statisticsService) {
        this.countDataHolder = statisticsService.getDataHolder();
        this.docCounter = statisticsService.getDocCounter();
    }

    @Pointcut("@annotation(org.stonexthree.domin.statistics.ViewAdd)")
    public void viewAddCut(){};

    @Around("viewAddCut()")
    public Object viewAdd(ProceedingJoinPoint joinPoint) throws Throwable {
        Signature signature = joinPoint.getSignature();
        if(!(signature instanceof MethodSignature)){
            log.warn("切点类型解析异常");
            throw new RuntimeException("切点类型解析异常");
        }
        MethodSignature methodSignature = (MethodSignature) signature;
        Method method =methodSignature.getMethod();
        Object[] args = joinPoint.getArgs();
        String[] argNames = methodSignature.getParameterNames();
        Object result = joinPoint.proceed(args);
        ViewAdd annotation = method.getAnnotation(ViewAdd.class);
        for(int i=0;i< argNames.length;i++){
            if(annotation.id().equals(argNames[i])&&args[i] instanceof String){
                docCounter.viewAdd(countDataHolder,(String)args[i]);
            }
        }
        return result;
    }

}
