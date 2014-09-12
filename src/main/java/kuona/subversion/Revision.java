package kuona.subversion;

import java.sql.Time;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Revision {
    int revisionNumber;
    private String username;
    private LocalTime revisionDate;
    private List<PathChange> changedPaths;
    private String commitMessage;
    private List<Hunk> hunks;

    public Revision(int revisionNumber, String username, LocalTime revisionDate) {

        this.revisionNumber = revisionNumber;
        this.username = username;
        this.revisionDate = revisionDate;
        this.changedPaths = new ArrayList<>();
        this.hunks = new ArrayList<>();
        this.commitMessage = "";
    }

    public Revision(int i, String group, Time timestamp) {

    }

    public void setCommitMessage(String commitMessage) {
        this.commitMessage = commitMessage;

    }

    public LocalTime getRevisionDate() {
        return revisionDate;
    }

    public int getRevisionNumber() {
        return revisionNumber;
    }

    public void setRevisionNumber(int revisionNumber) {
        this.revisionNumber = revisionNumber;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<PathChange> getChangedPaths() {
        return changedPaths;
    }

    public void addChangePath(String actionCode, String path) {
        changedPaths.add(new PathChange(actionCode, path));
    }

    public String commitMessage() {
        return commitMessage;
    }

    public Hunk hunk(int index) {
        return hunks.get(index);
    }

    public void addHunk(int i, int i1, int i2, int i3) {
        hunks.add(new Hunk(i, i1, i2, i3));
    }
}
