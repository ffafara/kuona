package kuona.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class JobWithDetailsTests {
    @Test
    public void mergingTwoEmptyListsReturnsEmptyList() {
        JobWithDetails job = new JobWithDetails();

        JobWithDetails result = job.merge(new JobWithDetails());

        assertThat(result.getBuilds().size(), is(0));
    }

    @Test
    public void mergingEmptyListLeavesListUnaltered() {
        JobWithDetails job = new JobWithDetails();
        job.builds.add(new Build());
        JobWithDetails result = job.merge(new JobWithDetails());

        assertThat(result.getBuilds().size(), is(1));
    }

}
