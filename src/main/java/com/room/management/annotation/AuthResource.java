package com.room.management.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE, ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface AuthResource {

    String value();

    String description() default "";

    String category() default "";

    boolean isPublic() default false;

    boolean requiresOwnership() default false;

    boolean isCoreResource() default false;

    int priority() default 1;
}
