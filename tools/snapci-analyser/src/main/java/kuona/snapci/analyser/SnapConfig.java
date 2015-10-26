package kuona.snapci.analyser;

import java.io.Serializable;

public class SnapConfig implements Serializable {
    private String url, user, password;

    public SnapConfig(String url, String user, String password) {
        this.url = url;
        this.user = user;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUser() {
        return user;
    }

    public String getPassword() {
        return password;
    }
}
