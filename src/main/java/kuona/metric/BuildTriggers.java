package kuona.metric;

import com.google.common.collect.Iterables;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public class BuildTriggers implements Iterable<BuildTrigger> {
    private static String genericMessages[] = {
            "Started by user",
            "Started by upstream project",
            "Started by an SCM change",
            "Started by GitHub push",
            "Rebuilds"

    };
    HashMap<String, BuildTrigger> triggers = new HashMap<>();

    public void add(String cause) {
        cause = normalize(cause);
        if (!triggers.containsKey(cause)) {
            triggers.put(cause, new BuildTrigger(cause));

        }

        triggers.get(cause).increment();
    }

    private String normalize(String cause) {
        for (String generic : genericMessages) {
            if (cause.startsWith(generic)) {
                cause = generic;
            }
        }
        return cause;
    }

    public int size() {
        return 1;
    }

    @Override
    public Iterator<BuildTrigger> iterator() {
        return triggers.values().iterator();
    }

    @Override
    public void forEach(Consumer<? super BuildTrigger> action) {
        triggers.values().forEach(action);
    }

    @Override
    public Spliterator<BuildTrigger> spliterator() {
        return triggers.values().spliterator();
    }

    public BuildTrigger get(int index) {
        return Iterables.get(triggers.values(), index);
    }
}
