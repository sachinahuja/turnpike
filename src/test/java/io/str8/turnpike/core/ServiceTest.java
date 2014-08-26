package io.str8.turnpike.core;

import io.str8.turnpike.annotations.PRINTER;
import io.str8.turnpike.core.Service;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.lang.model.element.Element;
import javax.lang.model.element.Name;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.*;

public class ServiceTest{

    @Mock
    private Element element;

    @Mock
    private Name name;

    private Service service;

    @Before
    public void setUp() throws Exception {
        initMocks(this);
        when(name.toString()).thenReturn("ValidIdentityService");
        when(element.toString()).thenReturn("com.amplify.service.ValidIdentityService");
        when(element.getSimpleName()).thenReturn(name);
        service = Service.from(element, null);
    }

    @Test
    public void serviceIsCreatedWithCorrectMetaData() throws Exception {
        PRINTER.println("Service Name: " + service.path);
        assert service.svcName.equals("ValidIdentityService");
        assert service.svcPackage.equals("com.amplify.service");
        assert service.path.equals("validIdentities");
    }
}