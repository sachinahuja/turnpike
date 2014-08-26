package io.str8.turnpike.core;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;

import javax.lang.model.element.*;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.TypeMirror;
import javax.lang.model.util.Types;
import java.util.*;

import static com.google.common.base.CaseFormat.LOWER_CAMEL;
import static com.google.common.base.CaseFormat.LOWER_UNDERSCORE;
import static com.google.common.base.CaseFormat.UPPER_CAMEL;
import static io.str8.turnpike.annotations.PRINTER.println;
import static javax.lang.model.type.TypeKind.*;

public class Endpoint {

    /*
     * Our Endpoint generation strategy is to keep the RESTful (resource-based) structure,
     * where the last element of the URI could be a verb or an action (for non CRUD operations).
     * e.g.: /users/{firstName:String}/{lastName:String}/poke
     * So for ALL services in the system it will be at most 2 levels of path elements -
     * service plural and method segment that does not identify the HTTP VERB
     * so, UserService.getAddress(String zip) will translate to:
     * GET /users/{zip:string}/address
     * OR
     * GET /users/address/{zip:string}
     * ALTHOUGH, ideally it should be:
     * GET /users/addresses/{zip:string}
     */

    private static Map<String, HTTP_METHOD> methodMap;
    private static Map<String, String> primToWrapper;

    private ExecutableElement element;
    private Service service;

    //stuff needed by templates
    String originalMethodName;
    String originalServiceFqcn;
    HTTP_METHOD httpMethod;
    String routeClassName;
    String methodPath;
    List<Param> params = new ArrayList<Param>();
    List<Param> pathParams = new ArrayList<Param>();
    List<Param> bodyParams = new ArrayList<Param>();

    private String uri;
    private int numBodyParams;


    private Endpoint(ExecutableElement element, Service service){
        this.element = element;
        this.service = service;
        loadInfoFromElement();

    }

    private void loadInfoFromElement() {
        originalMethodName = element.getSimpleName().toString();
        routeClassName = LOWER_CAMEL.to(UPPER_CAMEL, originalMethodName) + "_SvcID"+service.svcId;
        originalServiceFqcn = service.svcPackage +"."+ service.svcName;
        loadVerb();
        extractParams();
        path();
    }

    private void loadVerb(){
        String start = LOWER_CAMEL.to(LOWER_UNDERSCORE, originalMethodName).split("_")[0];
        httpMethod = methodMap.get(start);
        if(httpMethod==null)
            httpMethod = HTTP_METHOD.GET;

    }

    private void extractParams(){
        List<? extends VariableElement> params = element.getParameters();
        element.getTypeParameters();

        int i = 0;
        for(VariableElement param: params){
            extractParam(param, i);
            i++;
        }

        validateParams();

        TypeMirror ret = element.getReturnType();
        if(ret!=null && ret.getKind() != VOID)
            ret.hashCode();
    }

    private void validateParams() {
        if(this.httpMethod != HTTP_METHOD.POST && this.httpMethod != HTTP_METHOD.PUT)
            validateNoComplexObjectsInParamList();
        else
            validateAndMarkComplexObjectsComeAfterPrimitives();

    }

    private void validateAndMarkComplexObjectsComeAfterPrimitives() {
        boolean shouldBeComplex = false;
        for(Param p: params){
            if(shouldBeComplex && p.isPrimitive())
                throw new RuntimeException("ABORTING! ==> Found a primitive or String where complex object was expected: "+p.toString());
            shouldBeComplex = !p.isPrimitive();
            if(shouldBeComplex)
                numBodyParams++;
        }
    }

    private void validateNoComplexObjectsInParamList() {
        for(Param p: params){
            if(!p.isPrimitive() && !p.getName().equals("headers"))
                throw new RuntimeException("ABORTING! ==> Complex Objects in a non POST/PUT route's param list: "+originalMethodName +" => "+p.toString());
        }
    }

    private void extractParam(VariableElement paramElem, int i) {

        Param param = new Param();
        param.name = paramElem.getSimpleName().toString();
        param.index = i;
        param.svcPathSingular = service.pathSingular;
        TypeMirror paramType = paramElem.asType();
        TypeKind paramKind = paramType.getKind();
        if(paramKind.isPrimitive())
            param.type = primToWrapper.get(paramType.toString());
        else
            param.type = paramType.toString();


        if(param.isPrimitive())
            pathParams.add(param);
        else
            addToBodyParams(param, paramElem);
        params.add(param);
    }

    private void addToBodyParams(Param param, VariableElement elem) {

        TypeMirror m = elem.asType();
        TypeKind kind = m.getKind();
        println("Type Mirror for Body Param : " + kind);
        String name = m.toString();
        switch (kind){
            case DECLARED:
                if(name.contains("<"))
                    extractGenerics(param, name);
                else
                    param.addImport(name);
                break;
            case ARRAY:
                name = name.substring(0, name.indexOf("["));
                param.addImport(name);
                break;
        }
        bodyParams.add(param);
    }

    private void extractGenerics(Param param, String name){
        String myName = name.substring(0, name.indexOf("<"));
        param.addImport(myName);
        String types = name.substring(name.indexOf("<")+1, name.lastIndexOf(">"));
        Iterable<String> typesList = Splitter.on(",").omitEmptyStrings().trimResults().split(types);
        for(String type: typesList){
            if(type.contains("<"))
                extractGenerics(param, type);
            else
                param.addImport(type);
        }
    }


    private void path(){
        List<String> methodNameElements = methodNameToPathElements();
        resolveFirstMethodNameElement(methodNameElements);
        List<String> pathElems = new ArrayList<String>();
        this.methodPath = Joiner.on('-').join(methodNameElements);
        pathElems.add(service.path);
        for(Param p: params){
            pathElems.add(p.toString());
        }
        pathElems.add(this.methodPath);
        uri = Joiner.on('/').join(pathElems);
    }

    private void resolveFirstMethodNameElement(List<String> methodNameElements) {
        String first = methodNameElements.get(0);
        removeSegmentIfNeeded(methodNameElements, first);

        if(!methodNameElements.isEmpty())
            removeSegmentIfNeeded(methodNameElements, methodNameElements.get(0));

    }

    private void removeSegmentIfNeeded(List<String> methodNameElements, String first) {
        if(httpMethod.inQualifiers(first) || service.path.equals(first) || service.pathSingular.equals(first))
            methodNameElements.remove(0);
    }

    private List<String> methodNameToPathElements() {
        String snakeName = LOWER_CAMEL.to(LOWER_UNDERSCORE, originalMethodName);
        String pathElems[] = snakeName.split("_");
        return new ArrayList<String>(Arrays.asList(pathElems));

    }

    public static Endpoint from(ExecutableElement element, Service service) {
        methodMap = new HashMap<String, HTTP_METHOD>();
        HTTP_METHOD.GET.addEntries(methodMap);
        HTTP_METHOD.POST.addEntries(methodMap);
        HTTP_METHOD.PUT.addEntries(methodMap);
        HTTP_METHOD.DELETE.addEntries(methodMap);
        primToWrapper = new HashMap<String, String>();
        primToWrapper.put("int", "java.lang.Integer");
        primToWrapper.put("long", "java.lang.Long");
        primToWrapper.put("float", "java.lang.Float");
        primToWrapper.put("double", "java.lang.Double");
        primToWrapper.put("boolean", "java.lang.Boolean");
        primToWrapper.put("byte", "java.lang.Byte");
        primToWrapper.put("char", "java.lang.Character");
        primToWrapper.put("short", "java.lang.Short");
        return new Endpoint(element, service);
    }

    @Override
    public String toString() {
        return this.uri;
    }

    public String getOriginalMethodName() {
        return originalMethodName;
    }

    public String getOriginalServiceFqcn() {
        return originalServiceFqcn;
    }

    public HTTP_METHOD getHttpMethod() {
        return httpMethod;
    }

    public String getRouteClassName() {
        return routeClassName;
    }

    public String getMethodPath() {
        return methodPath;
    }

    public List<Param> getParams() {
        return params;
    }

    public String getUri() {
        return uri;
    }

    public List<Param> getPathParams() {
        return pathParams;
    }

    public List<Param> getBodyParams() {
        return bodyParams;
    }

    public boolean isSingleBodyParam(){
        return numBodyParams == 1;
    }
}
