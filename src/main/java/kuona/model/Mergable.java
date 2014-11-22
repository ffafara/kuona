package kuona.model;

public interface Mergable<T> {
    <T> T merge(T other);
}
