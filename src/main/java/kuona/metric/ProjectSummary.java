package kuona.metric;

import kuona.metric.CommitCount;
import org.joda.time.LocalDate;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ProjectSummary {
    public String name;
    Map<LocalDate, CommitCount> commitCounts;

    public ProjectSummary(String name, LocalDate from, LocalDate to) {
        this.commitCounts =  new HashMap<>();
        LocalDate eventDate = from;
        while (eventDate.isBefore(to)) {
            commitCounts.put(eventDate, new CommitCount(eventDate, 0));
            eventDate = eventDate.plusDays(1);
        }
        this.name = name;
    }

    public void increment(LocalDate revisionDate){
        if (!commitCounts.containsKey(revisionDate)) {
            return; //commitCounts.put(revisionDate, new CommitCount(revisionDate, 0));
        }
        commitCounts.get(revisionDate).increment();

    }

    public Iterator<CommitCount> getCounts() {
        return commitCounts.values().stream().sorted((a, b) -> a.compareTo(b)).iterator();
    }
}
