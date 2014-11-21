package kuona.config;

public class BuildServerSpec {
    String name;
    String url;
    String processor;
    String username;
    String password;

    BuildServerSpec() {
    }

    BuildServerSpec(String name, String url, String processor, String username, String password) {

        this.name = name;
        this.url = url;
        this.processor = processor;
        this.username = username;
        this.password = password;
    }

    public String getUrl() {
        return url;
    }

    public String getProcessor() {
        return processor;
    }

    public String getName() {
        return name;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
