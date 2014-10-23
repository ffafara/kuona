package kuona.model;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.hasItems;
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

    @Test
    public void mergedJobFromListsWithDifferentBuildsContainsBothBuilds() {
        JobWithDetails jobA = new JobWithDetails();
        final Build buildA = new Build(1, "");
        jobA.builds.add(buildA);
        JobWithDetails jobB = new JobWithDetails();
        final Build buildB = new Build(2, "");
        jobB.builds.add(buildB);

        JobWithDetails merged = jobA.merge(jobB);

        assertThat(merged.getBuilds().size(), is(2));
        assertThat(merged.getBuilds(), hasItems(buildA, buildB));
    }

}
