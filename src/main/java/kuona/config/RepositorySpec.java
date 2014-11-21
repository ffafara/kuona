package kuona.config;

import java.util.ArrayList;
import java.util.List;

public class RepositorySpec {
    public String url;
    public ArrayList<String> roots;
    private String processor;

    public RepositorySpec() {
        roots = new ArrayList<>();
    }
    public RepositorySpec(String url, String processor) {

        this.url = url;
        this.processor = processor;
    }

    public String getUrl() {
        return url;
    }

    public String getProcessor() {
        return processor;
    }

    public List<String> getRoots() {
        return roots;
    }
}
