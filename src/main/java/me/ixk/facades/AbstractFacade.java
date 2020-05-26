package me.ixk.facades;

import java.lang.reflect.Method;
import java.util.Arrays;
import me.ixk.ioc.Application;

public abstract class AbstractFacade {
    protected static Application app;

    public static void setApplication(Application application) {
        app = application;
    }

    public static <T> T invoke(
        Class<?> _class,
        String methodName,
        Class<T> returnType,
        Object... args
    ) {
        try {
            Object instance = app.make(_class);
            Method method = _class.getMethod(
                methodName,
                Arrays
                    .stream(args)
                    .map(Object::getClass)
                    .toArray(Class<?>[]::new)
            );
            Object result = method.invoke(instance, args);
            if (returnType == null) {
                return null;
            }
            return returnType.cast(result);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
