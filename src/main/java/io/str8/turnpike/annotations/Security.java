package io.str8.turnpike.annotations;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Map;

public class Security {

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Auth{
        Class<? extends Authenticator> authenticator() default Authenticator.class;
        Class<? extends Authorizer> authorizer() default Authorizer.class;
    }

    public static class Authenticator<T>{

        public T authenticate(Map headers){
            throw new RuntimeException("Unknown Subject");
        }
    }

    public static class Authorizer<T>{
        public boolean authorize(T subject){
            return false;
        }
    }

}
