package com.example.seating;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.springframework.stereotype.Component;

@Aspect
@Component
public class UserCheckAspect {

    @Pointcut("@annotation(com.example.seating.CheckUser)")
    public void getUserId(String userId){}

    public void before(){

    }
}
