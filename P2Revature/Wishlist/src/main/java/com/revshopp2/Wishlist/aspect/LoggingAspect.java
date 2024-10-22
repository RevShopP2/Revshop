package com.revshopp2.Wishlist.aspect;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class LoggingAspect {

    private static final Logger logger = LoggerFactory.getLogger(LoggingAspect.class);

    // Pointcut to target all methods in service classes
    @Pointcut("execution(* com.example.BuyerMicroService.service.*.*(..))")
    public void serviceLayer() {}

    // Around advice to log method execution and performance
    @Around("serviceLayer()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        long startTime = System.currentTimeMillis();
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getSignature().getDeclaringTypeName();

        logger.info("Executing {}.{}()", className, methodName);

        Object result;
        try {
            result = joinPoint.proceed(); // Execute the method
        } catch (Exception e) {
            logger.error("Exception in {}.{}(): {}", className, methodName, e.getMessage());
            throw e;
        }

        long elapsedTime = System.currentTimeMillis() - startTime;
        logger.info("Executed {}.{}() in {} ms", className, methodName, elapsedTime);
        return result;
    }
}