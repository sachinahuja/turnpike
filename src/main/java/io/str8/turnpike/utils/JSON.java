package io.str8.turnpike.utils;

import io.str8.turnpike.routing.exceptions.ModelConversionException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class JSON {
    private static final Logger LOG = LoggerFactory.getLogger(JSON.class);

    private static final ObjectMapper WRITER = new ObjectMapper();
    private static final String BLANK = "{}";


    public static String toJson(Object obj){
        try {
            String s = WRITER.writeValueAsString(obj);
            return s;
        } catch (Exception e) {
            e.printStackTrace();
            throw new ModelConversionException(obj);
        }
    }

    public static <T> T fromJson(String json, String name, Class<T> type){
        try {
            JsonNode jsonNode = WRITER.readTree(json);
            JsonNode field = jsonNode.get(name);
            if(field==null)
                throw new RuntimeException("Request Does Not Contain Parameter : "+name);
            return (T) WRITER.readValue(field, type);

        } catch (IOException e) {
            e.printStackTrace();
            throw new ModelConversionException(type);
        }
    }

}
