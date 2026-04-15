package org.example.product.datasource;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Aspect
@Component
@Order(1)
public class DataSourceAspect {

    // 拦截 ProductService 所有方法
    @Pointcut("execution(* org.example.product.service.*.*(..))")
    public void servicePointcut() {}

    @Around("servicePointcut()")
    public Object around(ProceedingJoinPoint point) throws Throwable {
        String method = point.getSignature().getName();

        // get/query/find/list/count 开头 -> 从库
        boolean isRead = method.startsWith("get")
                || method.startsWith("query")
                || method.startsWith("find")
                || method.startsWith("list")
                || method.startsWith("count");
        try {
            if (isRead) {
                DataSourceContextHolder.setSlave();
                System.out.println("=====> [读操作] " + method + " -> 从库(slave)");
            } else {
                DataSourceContextHolder.setMaster();
                System.out.println("=====> [写操作] " + method + " -> 主库(master)");
            }
            return point.proceed();
        } finally {
            DataSourceContextHolder.clear();
        }
    }
}