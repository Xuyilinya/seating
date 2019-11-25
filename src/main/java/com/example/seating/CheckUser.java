package com.example.seating;


import java.lang.annotation.*;

/**
 * @author Roy
 */
@Documented
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD,ElementType.TYPE})
@Inherited
public @interface CheckUser {
    String userId() default "";

}
