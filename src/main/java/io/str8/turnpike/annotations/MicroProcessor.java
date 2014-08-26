package io.str8.turnpike.annotations;



import io.str8.turnpike.core.Service;
import io.str8.turnpike.core.TurnpikeGenerator;
import org.apache.commons.io.FileUtils;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.RoundEnvironment;
import javax.annotation.processing.SupportedAnnotationTypes;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.tools.FileObject;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;
import javax.tools.StandardLocation;
import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static io.str8.turnpike.annotations.PRINTER.*;
import static javax.tools.Diagnostic.Kind.NOTE;

@SupportedAnnotationTypes({"io.str8.turnpike.annotations.Micro"})
public class MicroProcessor extends AbstractProcessor {

    private Messager messager;

    @Override
    public boolean process(Set<? extends TypeElement> typeElements, RoundEnvironment roundEnvironment) {
        try {
            messager = processingEnv.getMessager();
            PRINTER.messager = messager;
            List<Service> services = new ArrayList<Service>();

            Set<? extends Element> microElements = roundEnvironment.getElementsAnnotatedWith(Micro.class);
            if(microElements!=null && !microElements.isEmpty()) {
                println("Get ready to cruise the Turnpike!!");
                int i = 1;
                for (Element e : microElements) {
                    messager.printMessage(NOTE, "Exit " + i + " ==> " + e.toString());
                    Micro micro = e.getAnnotation(Micro.class);
                    String authProvider = micro.authClassName();
                    String user = micro.userClassName();
                    Service service = Service.from(e, processingEnv);
                    service.authProvider = authProvider;
                    service.user = user;
                    service.gen(new TurnpikeGenerator(processingEnv.getFiler()));
                    services.add(service);
                    i++;
                }
                new TurnpikeGenerator(processingEnv.getFiler()).genBootstrap(services);
                return true;
            }
            else
                return false;

        }catch(Exception e){
            e.printStackTrace();
            return false;
        }
    }

    @Override
    public SourceVersion getSupportedSourceVersion() {
        return SourceVersion.latestSupported();
    }
}
