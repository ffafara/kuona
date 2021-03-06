package kuona.subversion.analyser;

import org.joda.time.DateTime;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SubversionLogParser {

    public static final String COMMIT_BREAK = "^-----------+";
    private static final Pattern changePathPattern = Pattern.compile("^   " +
            "(A|D|M|R) " +   // Action code
            "(.*)");         // Path
    private static final Pattern hunkPattern = Pattern.compile("@@ -(\\d+),(\\d+) \\+(\\d+),(\\d+) @@");
    private static String SEPARATOR = " \\| ";
    private static final Pattern revisionSeparator = Pattern.compile("^r" +
            "(\\d+)" + SEPARATOR +
            "(\\S+)" + SEPARATOR +
            "(\\d{4}-\\d{2}-\\d{2}) (\\d{2}:\\d{2}:\\d{2}) ([-+])(\\d{2})(\\d{2}) \\([^\\)]+\\)" + SEPARATOR +
            "(\\d+) line(s)?");
    State state;
    private RevisionAccepter revisionAccepter;
    private Revision pendingRevision = null;

    public SubversionLogParser(RevisionAccepter revisionAccepter) {
        this.revisionAccepter = revisionAccepter;
        this.state = start;
    }

    void emit() {
        if (pendingRevision != null) {
            revisionAccepter.accept(pendingRevision);
        }
        pendingRevision = null;
    }

    public void parse(InputStream log) {

        final BufferedReader reader = new BufferedReader(new InputStreamReader(log));

        reader.lines().forEachOrdered((line) -> state = state.match(line));
        emit();
    }

    public interface RevisionAccepter {
        void accept(Revision r);
    }

    State paths = (String text) -> {
        Matcher matcher = changePathPattern.matcher(text);

        if (matcher.matches()) {
            pendingRevision.addChangePath(matcher.group(1), matcher.group(2));
            return this.paths;
        }

        if (text.matches(COMMIT_BREAK)) {
            emit();
            return this.start;
        }

        if (text.matches("")) {
            return this.commitMessage;
        }

        return this.start;
    };

    State commitMessage = (String text) -> {
        if (text.matches(COMMIT_BREAK)) {
            emit();
            return this.start;
        }

        Matcher matcher = hunkPattern.matcher(text);

        if (matcher.matches()) {
            pendingRevision.addHunk(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4))
            );
            return this.start;
        }

        pendingRevision.setCommitMessage(pendingRevision.commitMessage() + text + "\n");
        return this.commitMessage;
    };

    State start = (String text) -> {
        if (text.matches(COMMIT_BREAK)) {
            emit();
            return this.start;
        }
        Matcher matcher = revisionSeparator.matcher(text);

        if (matcher.matches()) {
            DateTime dateTime = DateTime.parse(matcher.group(3) + "T" + matcher.group(4) + matcher.group(5) + matcher.group(6) + ":" + matcher.group(7));
            pendingRevision = new Revision(Integer.parseInt(matcher.group(1)),
                    matcher.group(2),
                    dateTime
            );
            return this.start;
        }
        matcher = hunkPattern.matcher(text);

        if (matcher.matches()) {
            pendingRevision.addHunk(
                    Integer.parseInt(matcher.group(1)),
                    Integer.parseInt(matcher.group(2)),
                    Integer.parseInt(matcher.group(3)),
                    Integer.parseInt(matcher.group(4))
            );
        }

        if (text.matches("^Changed paths:$")) {
            return paths;
        }
        return this.start;

    };
}
