package kuona.subversion.analyser;

public class PathChange {
    private String actionCode;
    private String path;

    public PathChange(String actionCode, String path) {
        this.actionCode = actionCode;
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
