package io.str8.turnpike.routing.exceptions;

public class ModelConversionException extends RuntimeException {

    private static final String msg_serialize = "Could not convert model to JSON : ";
    private static final String msg_deserialize = "Could not convert JSON to model : ";

    public ModelConversionException(Object model) {
        super(msg_serialize + model);
    }

    public ModelConversionException(Class model) {
        super(msg_deserialize + model.getName());
    }
}
