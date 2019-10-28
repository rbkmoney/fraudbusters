package com.rbkmoney.fraudbusters.aspect;

import java.lang.annotation.*;


@Target({ElementType.ANNOTATION_TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Inherited
public @interface BasicMetric {
    String value() default "";

    String[] extraTags() default {};

    String description() default "";
}
