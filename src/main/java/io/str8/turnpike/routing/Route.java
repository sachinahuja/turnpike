package io.str8.turnpike.routing;

import io.netty.handler.codec.http.HttpRequest;
import io.str8.turnpike.utils.JSON;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public abstract class Route {

    private static final String BLANK = "";
    protected String svcUri;
    protected List<Action> actions = new ArrayList<Action>();

    public String execute(HttpRequest req, String[] resourcePath, String json, Map<String, Object> headers){

        String methodName = req.method().name();
        TurnpikeRequest tpReq = TurnpikeRequest.create()
                .json(json)
                .headers(headers);

        if(setReqForNamedMethodUri(resourcePath, methodName, tpReq))
            return call(tpReq);

        if(setReqForAnonymousMethodUri(resourcePath, methodName, tpReq))
            return call(tpReq);

        throw new RuntimeException("No Route Registered For URI : "+methodName+"\t"+req.uri());

    }

    private boolean setReqForNamedMethodUri(String[] resourcePath, String httpMethod, TurnpikeRequest req){
        if(resourcePath.length == 0)
            return false;

        String methodUri = resourcePath[resourcePath.length-1];
        int actionIndex = actions.indexOf(Action.create(methodUri,httpMethod));
        if(actionIndex == -1)
            return false;

        String[] params = Arrays.copyOfRange(resourcePath, 0, resourcePath.length-1);
        req.pathParams(params);
        req.action(actions.get(actionIndex));
        return true;

    }

    private boolean setReqForAnonymousMethodUri(String[] resourcePath, String httpMethod, TurnpikeRequest req){
        int actionIndex = actions.indexOf(Action.create(BLANK,httpMethod));
        if(actionIndex == -1)
            return false;

        req.pathParams(resourcePath);
        req.action(actions.get(actionIndex));
        return true;
    }

    protected String toJson(Object model){
        if(model == null)
            return "{}";

        return JSON.toJson(model);
    }

    protected void addRoute(Action action){
        this.actions.add(action);
    }

    protected abstract String call(TurnpikeRequest tpReq);
//    protected abstract String call(Action action, String[] params, String json);

}
