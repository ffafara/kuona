package kuona.jenkins.analyser;

public class BuildServerSpec {
    String url;
    String username;
    String password;

    BuildServerSpec() {
    }

    public BuildServerSpec(String name, String url, String processor, String username, String password) {

        this.url = url;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public void setServer(String server) {
        this.url = server;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
