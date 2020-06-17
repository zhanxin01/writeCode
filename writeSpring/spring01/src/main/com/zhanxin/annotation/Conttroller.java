package com.zhanxin.annotation;

import java.lang.annotation.*;

@Target(ElementType.TYPE)
@Documented
@Retention(RetentionPolicy.RUNTIME)
public @interface Conttroller {
    String value() default "";
}
