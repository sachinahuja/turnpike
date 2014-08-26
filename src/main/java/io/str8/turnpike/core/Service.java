package io.str8.turnpike.core;

import com.google.common.base.CaseFormat;
import org.atteo.evo.inflector.English;

import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Modifier;
import java.util.ArrayList;
import java.util.List;

import static io.str8.turnpike.annotations.PRINTER.println;

public class Service {

    private Element element;
    Processor processor;
    ProcessingEnvironment env;

    //stuff that'll be used by templates
    int svcId;
    String svcName;
    String svcPackage;
    String path;
    String pathSingular; //this is used for IDs and other service specific params
    List<Endpoint> endpoints = new ArrayList<Endpoint>();

    public String authProvider;
    public String user;


    private Service(Element e, ProcessingEnvironment env) {
        this.element = e;
        this.env = env;
        this.svcId = IDSeq.next();
        loadInfoFromElement();
        loadEndpoints();
    }

    private void loadInfoFromElement() {
        String svcFqcn = element.toString();
        svcName = element.getSimpleName().toString();
        svcPackage = svcFqcn.replace("." + svcName, "");
        pathSingular = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_CAMEL, svcName.replace("Service", ""));
        path = English.plural(pathSingular);
    }

    private void loadEndpoints(){

        List<? extends Element> elements = element.getEnclosedElements();
        for(Element child: elements){
            ElementKind kind = child.getKind();
            if(kind == ElementKind.METHOD && child.getModifiers().contains(Modifier.PUBLIC)){
                Endpoint endpoint = Endpoint.from((ExecutableElement) child, this);
                endpoints.add(endpoint);
                println("\tRoute ==> "+endpoint);
            }
        }


    }

    public static Service from(Element e, ProcessingEnvironment env) {
        return new Service(e, env);
    }

    public void gen(Generator generator) {
        generator.gen(this);
    }


    public int getSvcId() {
        return svcId;
    }

    public String getSvcName() {
        return svcName;
    }

    public String getSvcPackage() {
        return svcPackage;
    }

    public String getPath() {
        return path;
    }

    public String getPathSingular() {
        return pathSingular;
    }

    public List<Endpoint> getEndpoints() {
        return endpoints;
    }

    public String getSvcFqcn(){
        return this.svcPackage +"." + this.svcName;
    }

    public String getAuthProvider() {
        return authProvider;
    }

    public String getUser() {
        return user;
    }
}
