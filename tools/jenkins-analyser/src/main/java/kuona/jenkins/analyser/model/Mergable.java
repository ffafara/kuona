package kuona.jenkins.analyser.model;

public interface Mergable<T> {
    <T> T merge(T other);
}
