package io.str8.turnpike.routing;

public class ActionParam {

    private String name;
    private int index;
    private PARAM_TYPE type;

    public ActionParam(String name, PARAM_TYPE type){
        this.name = name;
        this.type = type;
    }


    public void setIndex(int index){
        this.index = index;
    }

    public Object parse(String value){
        switch (type){
            case INT:
                return getInt(value);
            case LONG:
                return getLong(value);
            case FLOAT:
                return getFloat(value);
            case DOUBLE:
                return getDouble(value);
            case BOOLEAN:
                return getBoolean(value);
            case STRING:
                 DEFAULT:
                return value;
        }
        return value;
    }


    public Long getLong(String value){
        return Long.valueOf(value);
    }

    public Integer getInt(String value){
        return Integer.valueOf(value);
    }

    public Float getFloat(String value){
        return Float.valueOf(value);
    }

    public Double getDouble(String value){
        return Double.valueOf(value);
    }

    public Boolean getBoolean(String value){
        return Boolean.valueOf(value);
    }


    public enum PARAM_TYPE{
        INT("Integer"), LONG("Long"), FLOAT("Float"), DOUBLE("Double"), BOOLEAN("Boolean"), STRING("String");
        private String type;
        PARAM_TYPE(String type){
            this.type = type;
        }

        public String getType(){
            return type;
        }
    }

}
