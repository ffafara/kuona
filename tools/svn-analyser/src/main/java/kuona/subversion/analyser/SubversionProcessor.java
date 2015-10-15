package kuona.subversion.analyser;

import org.joda.time.LocalDate;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SubversionProcessor {
    private final List<String> roots;

    public SubversionProcessor(RepositorySpec spec) {
        roots = new ArrayList<>();
    }


    Map<String, ArrayList<Revision>> collect() throws FileNotFoundException {

        Map<String, ArrayList<Revision>> revisionMap = new HashMap<>();
        revisionMap.put("summary", new ArrayList<>());
        for (String repoName : roots) {
            ArrayList<Revision> revisions = getRevisions(repoName);

            revisionMap.put(repoName, revisions);
            revisionMap.get("summary").addAll(revisions);
        }

        return revisionMap;
    }

    private ArrayList<Revision> getRevisions(String repoName) throws FileNotFoundException {
        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);
        parser.parse(new FileInputStream("svn/logs/" + repoName + ".txt"));
        return revisions;
    }


    public void analyse() {
        try {
            Map<String, ArrayList<Revision>> revisions = collect();

            Map<String, UserCommitCount> commitsByUser = new HashMap<>();

            ArrayList<ProjectSummary> projects = new ArrayList<>();

            LocalDate aYearAgo = new LocalDate().minusMonths(12);

            for (String name : revisions.keySet()) {
                ProjectSummary projectSummary = new ProjectSummary(name, aYearAgo, new LocalDate().plusDays(1));

                ArrayList<Revision> projectRevisions = revisions.get(name);
                for (Revision r : projectRevisions) {

                    LocalDate revisionDate = r.getRevisionDate().toLocalDate();

                    if (revisionDate.year().get() == new LocalDate().year().get()) {
                        projectSummary.increment(revisionDate);


                        if (!commitsByUser.containsKey(r.getUsername())) {
                            commitsByUser.put(r.getUsername(), new UserCommitCount(r.getUsername()));
                        }
                        commitsByUser.get(r.getUsername()).increment();
                    }
                }

                projects.add(projectSummary);
            }

            commitsByUser.values().stream().forEach(uc -> System.out.println(uc.getUsername() + " Made " + uc.getCount()));

            Object[] commits = commitsByUser.values().stream().sorted((a, b) -> (int) (b.count - a.count)).limit(50).toArray();
        } catch (Exception e) {
            throw new RuntimeException("Subversion processing failed ", e);
        }
    }
}
