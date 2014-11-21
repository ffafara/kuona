package kuona.metric;

import org.joda.time.LocalDate;

class CommitCount {
    LocalDate timestamp;
    long count;


    public long getTimestamp() {
        return timestamp.toDateMidnight().getMillis();
    }

    public int compareTo(CommitCount other) {
        return timestamp.compareTo(other.timestamp);
    }

    public long getCount() {
        return count;
    }

    public CommitCount(LocalDate timestamp, Integer count) {

        this.timestamp = timestamp;
        this.count = count;
    }

    public LocalDate getDate() {
        return timestamp;
    }

    public void increment() {
        count += 1;
    }
}
