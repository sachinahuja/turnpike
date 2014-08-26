package io.str8.turnpike.routing;

import io.str8.turnpike.core.HTTP_METHOD;

import java.util.ArrayList;
import java.util.List;

public class Action {

    public static final String BLANK_URI = "";

    public String uri;
    public HTTP_METHOD method;
    public int id;
    public List<ActionParam> params = new ArrayList<ActionParam>();

    public Action(String uri, HTTP_METHOD method){
        this.uri = uri;
        this.method = method;
    }

    public static Action create(String uri, String method){
        return new Action(uri, HTTP_METHOD.get(method));
    }

    public Action add(ActionParam param){
        int num = params.size();
        param.setIndex(num);
        params.add(param);
        return this;
    }

    public int paramsLength(){
        return params.size();
    }


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Action action = (Action) o;

        if (method != action.method) return false;
        if (!uri.equals(action.uri)) return false;

        return true;
    }

    @Override
    public int hashCode() {
        int result = uri.hashCode();
        result = 31 * result + method.hashCode();
        return result;
    }
}
