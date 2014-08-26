package io.str8.turnpike.routing;

import io.str8.turnpike.utils.JSON;

import java.util.Map;

public class TurnpikeRequest {

    public int actionId;

    Action action;
    String[] pathParams;
    String json;
    Map<String, Object> headers;

    static TurnpikeRequest create(){
        return new TurnpikeRequest();
    }

    TurnpikeRequest action(Action action) {
        this.action = action;
        this.actionId = action.id;
        return this;
    }

    TurnpikeRequest pathParams(String[] pathParams) {
        this.pathParams = pathParams;
        return this;
    }

    TurnpikeRequest json(String json) {
        this.json = json;
        return this;
    }

    TurnpikeRequest headers(Map<String, Object> headers) {
        this.headers = headers;
        return this;
    }

    public boolean validNumberOfParams(){
        if(pathParams==null){
            if(action.paramsLength() == 0)
                return true;
            else
                return false;
        } else {

            return action.paramsLength() == pathParams.length;
        }
    }

    public Action getAction() {
        return action;
    }

    public String[] getPathParams() {
        return pathParams;
    }

    public String getJson() {
        return json;
    }

    public Map<String, Object> getHeaders() {
        return headers;
    }

    public Object getParam(int index){
        return action.params.get(index).parse(pathParams[index]);
    }

    public <T> T getParam(String name, Class<T> type){
        return JSON.fromJson(json, name, type);
    }

}
