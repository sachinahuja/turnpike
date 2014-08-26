package io.str8.turnpike.core;

import java.util.List;

public interface Generator {

    void gen(Service service);
    void genBootstrap(List<Service> services);
}
