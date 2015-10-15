package kuona.subversion.analyser;

import java.util.ArrayList;
import java.util.List;

public class RepositorySpec {
    public String url;
    public ArrayList<String> roots;

    public RepositorySpec() {
        roots = new ArrayList<>();
    }
    public RepositorySpec(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }
    public List<String> getRoots() {
        return roots;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRoots(List<String> roots) {
        this.roots.addAll(roots);
    }
}
