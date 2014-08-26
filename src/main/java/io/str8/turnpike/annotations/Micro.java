package io.str8.turnpike.annotations;

import io.str8.turnpike.core.Endpoint;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.SOURCE)
public @interface Micro {
    String authClassName() default "";
    String userClassName() default "";
}
