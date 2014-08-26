package io.str8.turnpike.core;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Param {

    private static List<String> IDs = Arrays.asList(new String[]{"id", "Id", "ID"});


    String name;
    String type;
    int index;
    String svcPathSingular;
    List<String> imports = new ArrayList<String>();

    @Override
    public String toString() {
        return "{"+name+":"+getDisplayType()+"}";

//        return "Param @ "+index+" ==> \nName: "+name+"\nType: "+type+"\nDisplay Type: "+getDisplayType()+"\n<== For service "+svcPathSingular+" as identifer: "+isId();
    }

    void addImport(String className){
        this.imports.add(className);
    }

    public String getDisplayType() {
        if(isPrimitive())
            return Iterables.getLast(Splitter.on('.').split(type));
        else
            return type;
    }

    public boolean isPrimitive(){
        return type.startsWith("java.lang");
    }

    public boolean isHeaders(){
        return name.equals("headers");
    }

    //this will be relevant if we want to create URLs with resource/{actionId}/action/{other_params} format
    private boolean isId() {
        if(IDs.contains(name))
            return true;

        for(String suffix: IDs){
            if ( (svcPathSingular+suffix).equals(name)){
                return true;
            }
        }

        return false;
    }

    public String getName() {
        return name;
    }

    public String getSvcPathSingular() {
        return svcPathSingular;
    }

    public String getType() {
        return type;
    }

    public List<String> getImports() {
        System.out.println("NUMBER OF IMPORTS FOR PARAM : "+name+" ====> "+imports.size());
        return imports;
    }
}
