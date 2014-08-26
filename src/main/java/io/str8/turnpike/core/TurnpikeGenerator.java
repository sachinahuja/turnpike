package io.str8.turnpike.core;

import org.stringtemplate.v4.ST;
import org.stringtemplate.v4.STGroup;
import org.stringtemplate.v4.STGroupFile;
import org.stringtemplate.v4.StringRenderer;

import javax.annotation.processing.Filer;
import javax.tools.JavaFileManager;
import javax.tools.JavaFileObject;

import java.io.IOException;
import java.io.Writer;
import java.net.URI;
import java.util.List;

import static io.str8.turnpike.annotations.PRINTER.*;

public class TurnpikeGenerator implements Generator {

    private Filer filer;
    private static String PFX = "RoutesForSvc";
    private static String PKG = "io.str8.gen";

    public TurnpikeGenerator(Filer filer) {
        this.filer = filer;
    }


    @Override
    public void gen(Service service) {
        try {
            println("Generating Service with: \n" + "Package: " + service.svcPackage + "\nClass: " + service.svcName);
            String routeName = PFX + service.getSvcId();
            String routeContent = genRoute(service);
            writeSrc(routeName, routeContent);
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    private void writeSrc(String fqcn, String content) throws IOException {
        JavaFileObject j = filer.createSourceFile(PKG +"."+ fqcn);
        Writer writer = j.openWriter().append(content);
        writer.flush();
        writer.close();
        URI uri = j.toUri();
        println("Written: "+uri.getPath());

    }

    private String genRoute(Service service){
        STGroup stg = new STGroupFile("templates/router.stg");
        stg.registerRenderer(String.class, new StringRenderer());
        ST route = stg.getInstanceOf("routeClass");
        route.add("service", service);
        return route.render();
    }

    @Override
    public void genBootstrap(List<Service> services) {
        println("Generate Route Loader & Server");
        String rlContent = genRouteLoader(services);
        String serverContent = genServer();
        try {
            writeSrc("_RouteLoader_", rlContent);
            writeSrc("Server", serverContent);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private String genServer(){
        STGroup stg = new STGroupFile("templates/bootstrap.stg");
        ST server = stg.getInstanceOf("server");
        return server.render();
    }

    private String genRouteLoader(List<Service> services) {
        STGroup stg = new STGroupFile("templates/bootstrap.stg");
        ST routeLoader = stg.getInstanceOf("routeLoader");
        routeLoader.add("services", services);
        return routeLoader.render();
    }
}
