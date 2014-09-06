package ddg.generator;

import ddg.utils.ChannelTools;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.channels.Channels;

public class Site {
    public void createDirectory(String name) {
        File theDir = new File(name);

        if (!theDir.exists()) {
            boolean result = false;

            theDir.mkdir();
        }
    }

    public void createFile(String filename, InputStream contents) {
        try {
            File file = new File(filename);

            if (!file.exists()) {
                OutputStream os = new FileOutputStream(filename);

                ChannelTools.fastChannelCopy(Channels.newChannel(contents), Channels.newChannel(os));
                os.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
