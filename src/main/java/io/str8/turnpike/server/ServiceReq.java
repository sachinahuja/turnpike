package io.str8.turnpike.server;

public class ServiceReq<T> {

    private String apiVersion;
    private T service;

    public ServiceReq(T service, String apiVersion){
        this.apiVersion = apiVersion;
        this.service = service;
    }

}
