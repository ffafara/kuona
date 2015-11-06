package kuona.web.model;

public class Metric {
    private String id;
    private String name;

    public Metric(String id, String name, byte[] data){
        this.id = id;
        this.data = data;
        this.name = name;
    }

    private byte[] data;

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public byte[] getData() {
        return data;
    }
}
