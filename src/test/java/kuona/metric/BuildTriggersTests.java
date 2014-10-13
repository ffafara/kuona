package kuona.metric;

import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class BuildTriggersTests {
    @Test
    public void acceptsTrigger() {
        BuildTriggers triggers = new BuildTriggers();

        triggers.add("Started by user graham");

        assertThat(triggers.size(), is(1));
    }

    @Test
    public void canIterateOverTriggers() {
        BuildTriggers triggers = new BuildTriggers();

        triggers.add("Started by user graham");

        assertThat(triggers.size(), is(1));

        List<BuildTrigger> found = new ArrayList<>();

        for (BuildTrigger t : triggers) {
            found.add(t);
        }

        assertThat(found.size(), is(1));
    }

    @Test
    public void usernameTrimmedFromStartedBy() {
        BuildTriggers triggers = new BuildTriggers();

        triggers.add("Started by user graham");

        assertThat(triggers.iterator().next().getCause(), is("Started by user"));

        List<BuildTrigger> found = new ArrayList<>();

        for (BuildTrigger t : triggers) {
            found.add(t);
        }

        assertThat(found.size(), is(1));
    }

    @Test
    public void addingCauseMultipleTimesIncrementsCount() {
        BuildTriggers triggers = new BuildTriggers();

        triggers.add("Started by user graham");
        triggers.add("Started by user graham");
        triggers.add("Started by user tom");

        assertThat(triggers.size(), is(1));
        assertThat(triggers.iterator().next().getCount(), is(3));

    }
}
