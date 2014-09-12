package kuona.subversion;

public class Hunk {
    private int originalStart;
    private int originalLength;
    private int newStart;
    private int newLength;

    public Hunk(int originalStart, int originalLength, int newStart, int newLength) {
        this.originalStart = originalStart;
        this.originalLength = originalLength;
        this.newStart = newStart;
        this.newLength = newLength;
    }

    public int changedLines() {
        return Math.abs(newLength - originalLength);
    }
}
