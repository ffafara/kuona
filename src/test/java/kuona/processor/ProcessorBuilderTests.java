package kuona.processor;

import kuona.config.RepositorySpec;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;


public class ProcessorBuilderTests {
    @Test
    public void canInstantiateSubversionProcessor() {
        ProcessorBuilder builder = new ProcessorBuilder();
        RepositorySpec spec = new RepositorySpec();

        final RepositoryProcessor processor = builder.build(spec, "Subversion", RepositoryProcessor.class);

        assertThat(processor.getName(), is("Subversion"));
    }
}
