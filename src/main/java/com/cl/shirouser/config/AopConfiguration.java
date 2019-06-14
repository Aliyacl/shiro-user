package com.cl.shirouser.config;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.context.annotation.Configuration;

@Aspect
@Configuration
public class AopConfiguration {

    @Pointcut("execution")
    public void executeService(){

    }

}
