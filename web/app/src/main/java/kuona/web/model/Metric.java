package kuona.web.model;

public class Metric {
    private String id;

    public Metric(String id, byte[] data) {
        this.id = id;
        this.data = data;
    }

    private byte[] data;

    public String getId() {
        return id;
    }

    public byte[] getData() {
        return data;
    }
}
