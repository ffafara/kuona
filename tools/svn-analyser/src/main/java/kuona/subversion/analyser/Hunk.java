package kuona.subversion.analyser;

public class Hunk {
    private int originalLength;
    private int newLength;

    public Hunk(int originalStart, int originalLength, int newStart, int newLength) {
        this.originalLength = originalLength;
        this.newLength = newLength;
    }

    public int changedLines() {
        return Math.abs(newLength - originalLength);
    }
}
