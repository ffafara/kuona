package kuona.generator;

import kuona.utils.ChannelTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

import static kuona.utils.Utils.puts;

public class Site {
    private final boolean verboase;

    public Site() {
        this(false);
    }

    public Site(boolean verboase) {
        this.verboase = verboase;
    }

    public void createDirectory(String name) {
        File theDir = new File(name);

        if (!theDir.exists()) {
            trace("Creating directory " + name);
            boolean result = false;

            theDir.mkdir();
        }
    }

    public void createFile(String filename, InputStream contents) {
        try {
            trace("Creating file " + filename);
            File file = new File(filename);

            OutputStream os = new FileOutputStream(filename);

            ChannelTools.fastChannelCopy(Channels.newChannel(contents), Channels.newChannel(os));
            os.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void trace(String message) {
        if (verboase)
            puts(message);
    }
}
