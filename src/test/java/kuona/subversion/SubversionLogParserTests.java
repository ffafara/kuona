package kuona.subversion;

import org.junit.Test;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

public class SubversionLogParserTests {
    @Test
    public void acceptsEmptyStream() {
        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);

        parser.parse(new ByteArrayInputStream("".getBytes()));

        assertThat(revisions.size(), is(0));
    }

    @Test
    public void recognisesRevisionLine() {
        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);

        parser.parse(new ByteArrayInputStream((
                "------------------------------------------------------------------------\n" +
                        "r123 | foobar23_ | 2011-07-17 11:17:49 -0400 (Sun, 17 Jul 2011) | 1 line").getBytes()));
        assertThat(revisions.size(), is(1));
        assertThat(revisions.get(0).getRevisionNumber(), is(123));
        assertThat(revisions.get(0).getUsername(), is("foobar23_"));
        assertThat(revisions.get(0).getRevisionDate().hourOfDay().get(), is(11));


    }

    @Test
    public void recognisesChangedPaths() {
        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);

        parser.parse(new ByteArrayInputStream((
                "------------------------------------------------------------------------\n" +
                        "r123 | foobar23_ | 2011-07-17 11:17:49 -0400 (Sun, 17 Jul 2011) | 1 line\n" +
                        "Changed paths:\n" +
                        "   M /trunk/grappelli/templates/admin/edit_inline/stacked.html\n" +
                        "\n" +
                        "message line 1\n" +
                        "message line 2\n" +
                        "\n"
        ).getBytes()));
        assertThat(revisions.size(), is(1));
        assertThat(revisions.get(0).getChangedPaths().size(), is(1));
        assertThat(revisions.get(0).getChangedPaths().get(0).getPath(), is("/trunk/grappelli/templates/admin/edit_inline/stacked.html"));
        assertThat(revisions.get(0).commitMessage(), is("message line 1\nmessage line 2\n\n"));
    }

    @Test
    public void recognisesChangedHunk() {
        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);

        parser.parse(new ByteArrayInputStream((
                "------------------------------------------------------------------------\n" +
                        "r123 | foobar23_ | 2011-07-17 11:17:49 -0400 (Sun, 17 Jul 2011) | 1 line\n" +
                        "Changed paths:\n" +
                        "   M /trunk/grappelli/templates/admin/edit_inline/stacked.html\n" +
                        "\n" +
                        "message line 1\n" +
                        "message line 2\n" +
                        "\n" +
                        "@@ -116,6 +116,7 @@\n"

        ).getBytes()));
        assertThat(revisions.get(0).hunk(0).changedLines(), is(1));
    }


    @Test
    public void acceptanceTest() {
        String logData = "------------------------------------------------------------------------\n" +
                "r1457 | sehmaschine | 2011-07-17 11:17:49 -0400 (Sun, 17 Jul 2011) | 1 line\n" +
                "Changed paths:\n" +
                "   M /trunk/grappelli/templates/admin/edit_inline/stacked.html\n" +
                "\n" +
                "added collapsibles for h4 within stacked-inlines (resolved issue #388)\n" +
                "\n" +
                "Index: templates/admin/edit_inline/stacked.html\n" +
                "===================================================================\n" +
                "--- templates/admin/edit_inline/stacked.html\t(revision 1456)\n" +
                "+++ templates/admin/edit_inline/stacked.html\t(revision 1457)\n" +
                "@@ -116,6 +116,7 @@\n" +
                "                     });\n" +
                "                 });\n" +
                "                 form.grp_collapsible();\n" +
                "+                form.find(\".collapse\").grp_collapsible();\n" +
                "             }\n" +
                "         });\n" +
                "         \n" +
                "\n" +
                "------------------------------------------------------------------------\n" +
                "r1456 | sehmaschine | 2011-07-17 11:08:29 -0400 (Sun, 17 Jul 2011) | 1 line\n" +
                "Changed paths:\n" +
                "   M /trunk/docs/changelog.rst\n" +
                "   M /trunk/docs/conf.py\n" +
                "   M /trunk/docs/index.rst\n" +
                "   M /trunk/grappelli/__init__.py\n" +
                "   M /trunk/setup.py\n" +
                "\n" +
                "updated trunk-version to 2.3.4\n" +
                "\n" +
                "Index: __init__.py\n" +
                "===================================================================\n" +
                "--- __init__.py\t(revision 1455)\n" +
                "+++ __init__.py\t(revision 1456)\n" +
                "@@ -1 +1 @@\n" +
                "-VERSION = '2.3.3'\n" +
                "\\ No newline at end of file\n" +
                "+VERSION = '2.3.4'\n" +
                "\\ No newline at end of file\n" +
                "\n" +
                "------------------------------------------------------------------------\n";

        ArrayList<Revision> revisions = new ArrayList<>();
        SubversionLogParser parser = new SubversionLogParser(revisions::add);

        parser.parse(new ByteArrayInputStream(logData.getBytes()));

        assertThat(revisions.size(), is(2));
    }
}
