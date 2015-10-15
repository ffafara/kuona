package kuona.subversion.analyser;

public interface State {
    public State match(String text);
}
