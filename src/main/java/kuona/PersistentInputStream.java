package kuona;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class PersistentInputStream extends InputStream {
    private OutputStream tee;
    private InputStream base;

    public PersistentInputStream(InputStream base) {

        this.base = base;
    }

    public PersistentInputStream(OutputStream tee, InputStream base) {
        this.tee = tee;

        this.base = base;
    }

    @Override
    public int read() throws IOException {
        final int c = base.read();
        if (tee != null) tee.write(c);
        return c;
    }
}
