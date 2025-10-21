package com.example.demo.aspect;

import jakarta.servlet.http.HttpServletRequest;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;


import java.util.Arrays;

/**
 * 接口日志切面：拦截所有Controller接口，打印请求信息和入参
 */

@Aspect
@Component // 注入Spring容器
public class ApiLogAspect {

    private static final Logger log = LoggerFactory.getLogger(ApiLogAspect.class);

    // 切入点：拦截所有Controller的public方法（可根据实际包路径调整）
    @Pointcut("execution(public * com.example.demo.controller..*.*(..))")
    public void apiLogPointcut() {}

    // 环绕通知：在方法执行前后打印日志（推荐用环绕通知，可同时记录开始和结束）
    @Around("apiLogPointcut()")
    public Object aroundApi(ProceedingJoinPoint joinPoint) throws Throwable {
        // 1. 获取请求上下文信息
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes == null) {
            // 非HTTP请求场景（如定时任务），直接执行方法
            return joinPoint.proceed();
        }
        HttpServletRequest request = attributes.getRequest();

        StringBuilder logMsg = new StringBuilder();
        logMsg.append("========================== 接口请求开始 ==========================\n")
                .append("请求URL：").append(request.getRequestURL().toString()).append("\n")
                .append("请求方法：").append(request.getMethod()).append("\n")
                .append("请求IP：").append(request.getRemoteAddr()).append("\n")
                .append("请求类名：").append(joinPoint.getSignature().getDeclaringTypeName()).append("\n")
                .append("请求方法名：").append(joinPoint.getSignature().getName()).append("\n")
                .append("请求入参：").append(Arrays.toString(joinPoint.getArgs())).append("\n") // 入参数组转字符串
                .append("===========================接口结束========================================");

        log.info(logMsg.toString());
        // 3. 执行目标方法（放行）
        Object result = joinPoint.proceed();

        // （可选）打印响应结果（如果需要）
        // log.info("响应结果：{}", result);

        return result;
    }

    @AfterThrowing(pointcut = "apiLogPointcut()", throwing = "e")
    public void afterThrowing(JoinPoint joinPoint, Exception e) {
        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        if (attributes != null) {
            HttpServletRequest request = attributes.getRequest();

            StringBuilder errorMsg = new StringBuilder();
            errorMsg.append("========================== 接口请求异常 ==========================\n")
                    .append("请求URL：").append(request.getRequestURL()).append("\n")
                    .append("请求方法：").append(joinPoint.getSignature().getDeclaringTypeName())
                    .append(".").append(joinPoint.getSignature().getName()).append("\n")
                    .append("请求参数：").append(Arrays.toString(joinPoint.getArgs())).append("\n")
                    .append("异常信息：").append(e).append("\n") // 日志框架会处理异常堆栈
                    .append("===================================================================");

            log.error(errorMsg.toString());
        }
    }
}