package kuona.processor;

import kuona.config.RepositorySpec;

import java.lang.reflect.Constructor;

public class ProcessorBuilder {
    public <T> T build(RepositorySpec spec, String name, Class<? extends T> t) {
        return createInstance(spec, name, "kuona.processor." + name + "Processor");
    }

    private <T> T createInstance(RepositorySpec spec, String... names) {
        T instance = null;
        for (String name : names) {
            try {
                final Class<? extends T> aClass1 = (Class<? extends T>) Class.forName(name);

                final Constructor<?> constructor = aClass1.getConstructor(RepositorySpec.class);
                instance = (T) constructor.newInstance(spec);
            } catch (Exception e) {

            }
            if (instance != null) return instance;
        }
        throw new RuntimeException("Failed to create processor " + names);
    }
}
