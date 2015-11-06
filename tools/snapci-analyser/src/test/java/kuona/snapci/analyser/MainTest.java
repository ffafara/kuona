package kuona.snapci.analyser;

import org.apache.commons.cli.CommandLine;
import org.junit.Test;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class MainTest {

    @Test
    public void acceptsShortParametersOnCommandLine() {
        final String[] args = {"-u", "http://localhost", "-n", "testname"};

        CommandLine actual = Main.parseOptions(args);

        assertThat(actual.getOptions().length, is(2));
        assertTrue(actual.hasOption("u"));
        assertTrue(actual.hasOption("n"));
        assertThat(actual.getOptionValue("u"), is("http://localhost"));
        assertThat(actual.getOptionValue("n"), is("testname"));
    }

    @Test
    public void acceptsParametersOnCommandLine() {
        final String[] args = {"-url", "http://localhost", "-name", "testname"};

        CommandLine actual = Main.parseOptions(args);

        assertThat(actual.getOptions().length, is(2));
        assertTrue(actual.hasOption("url"));
        assertTrue(actual.hasOption("name"));
        assertThat(actual.getOptionValue("url"), is("http://localhost"));
        assertThat(actual.getOptionValue("name"), is("testname"));
    }

    @Test (expected = RuntimeException.class)
    public void failsWithoutRequiredParameters() {
        final String[] args = {"-n", "testname"};

        CommandLine actual = Main.parseOptions(args);
    }

}