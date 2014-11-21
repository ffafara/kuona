package kuona.metric;

public class UserCommitCount {
    public String username;
    public long count;

    public UserCommitCount(String username) {
        this.count = 0;
        this.username = username;
    }

    public void increment() {
        count += 1;
    }

    public String getUsername() {
        return username;
    }

    public long getCount() {
        return count;
    }
}
