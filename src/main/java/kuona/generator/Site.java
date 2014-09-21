package kuona.generator;

import kuona.utils.ChannelTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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
            boolean result = false;

            theDir.mkdir();
        }
    }

    public void createFile(String filename, InputStream contents) {
        try {
            final Path path = Paths.get(new File(filename).getParent());

            if (!path.toFile().exists()) {

                Files.createDirectories(path);
            }

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
