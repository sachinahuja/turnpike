package io.str8.turnpike.core;

import io.str8.turnpike.annotations.PRINTER;
import io.str8.turnpike.core.Endpoint;
import io.str8.turnpike.core.HTTP_METHOD;
import io.str8.turnpike.core.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.VariableElement;

import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

public class EndpointTest{

    @Mock
    private Element element;

    @Mock
    private ExecutableElement methodElement;

    @Mock
    private Name name;

    @Mock
    private Name methodName;

    @Mock
    VariableElement param;

    private Service service;
    private Endpoint endpoint;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(name.toString()).thenReturn("IdentityService");
        when(element.toString()).thenReturn("com.amplify.service.IdentityService");
        when(element.getSimpleName()).thenReturn(name);
        service = Service.from(element, null);

        when(methodName.toString()).thenReturn("getSomething");
        when(methodElement.getSimpleName()).thenReturn(methodName);
        endpoint = Endpoint.from(methodElement, service);



    }

    @Test
    public void endpointIsCreatedWithCorrectMetaData() throws Exception {

        PRINTER.println("Endpoint Route Name: " + endpoint.routeClassName);
        assert endpoint.httpMethod == HTTP_METHOD.GET;
        assert endpoint.originalMethodName.equals("getSomething");
        assert endpoint.originalServiceFqcn.equals("com.amplify.service.IdentityService");
        assert endpoint.routeClassName.equals("GetSomething_SvcID1");

    }
}