package com.dnt.data.standard.server.aspect;

import com.dnt.data.standard.server.tools.redis.cache.CacheService;
import com.dnt.data.standard.server.annotation.CacheLock;
import com.dnt.data.standard.server.web.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.Method;

/**
 * @description: 5秒钟不允许重复保存 apo 切面 <br>
 * @date: 2021/7/20 下午6:08 <br>
 * @author: chenhl <br>
 * @version: 1.0 <br>
 */
@Aspect
@Configuration
@Slf4j
public class LockMethodInterceptor {
    @Autowired
    private CacheService cacheService;

    /**保存方法拦截 10秒内不允许重复提交**/
    @Pointcut("execution(public * *(..)) && @annotation(com.dnt.data.standard.server.annotation.CacheLock)")
    public void saveIn10Seconds(){
        if(log.isInfoEnabled()) {
            log.info("LockMethodInterceptor-->saveIn5Seconds 5秒后保存切点");
        }
    }
    /**前置通知**/
    @Before( "saveIn10Seconds()")
    public void doInterceptorSave5Seconds(JoinPoint jp){
        if(log.isInfoEnabled()) {
            log.info("LockMethodInterceptor.doInterceptorSave5Seconds 切面 前置通知");
        }

        MethodSignature signature = (MethodSignature) jp.getSignature();
        Method method = signature.getMethod();
        CacheLock lock = method.getAnnotation(CacheLock.class);
        /**获取方法上的注解信息做为key**/
        String key = lock.prefix();
        /**10 秒内重复保存会报错误提示**/
        Boolean bLock = cacheService.lock("redisLock",key,key+" model lock",5);
        log.info(method.getName());
        if(!bLock){
            throw new ServiceException("5秒内不允许重复提交");
        }
    }
}
