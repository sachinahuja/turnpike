package io.str8.turnpike.core;

import java.util.Map;

public enum HTTP_METHOD {

    GET("get","find","all"),
    POST("add", "create", "post"),
    PUT("update", "put"),
    DELETE("remove", "delete");

    private String[] qualifiers;

    HTTP_METHOD(String... qualifiers){
        this.qualifiers = qualifiers;
    }

    public void addEntries(Map<String, HTTP_METHOD> map){
        for(String w: qualifiers){
            map.put(w, this);
        }
    }

    public boolean inQualifiers(String portion) {
        for(String q: qualifiers){
            if(q.equals(portion))
                return true;
        }
        return false;
    }

    public String getVal(){
        return this.toString();
    }

    public static HTTP_METHOD get(String method){
        if(method.equalsIgnoreCase("get"))
            return GET;

        if(method.equalsIgnoreCase("post"))
            return POST;

        if(method.equalsIgnoreCase("put"))
            return PUT;

        if(method.equalsIgnoreCase("delete"))
            return DELETE;

        return null;
    }
}
