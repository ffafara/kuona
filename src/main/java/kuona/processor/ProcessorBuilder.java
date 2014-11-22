package kuona.processor;

import java.lang.reflect.Constructor;

public class ProcessorBuilder {
    public <T, P> T build(P spec, String name, Class<? extends T> t) {
        return createInstance(spec, name, "kuona.processor." + name + "Processor");
    }

    private <T, P> T createInstance(P spec, String... names) {
        T instance = null;
        for (String name : names) {
            try {
                final Class<? extends T> aClass1 = (Class<? extends T>) Class.forName(name);

                final Constructor<?> constructor = aClass1.getConstructor(spec.getClass());

                instance = (T) constructor.newInstance(spec);

            } catch (Exception e) {

            }
            if (instance != null) return instance;
        }
        throw new RuntimeException("Failed to create processor " + names);
    }
}
