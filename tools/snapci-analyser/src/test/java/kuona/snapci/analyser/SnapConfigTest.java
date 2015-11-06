package kuona.snapci.analyser;

import org.apache.http.HttpHost;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

public class SnapConfigTest {

    @Test
    public void shouldParseUrl() {
        final String testUrl = "http://some.site.com:9123/test";
        final String testUser = "testUser";
        final String testPassword = "testPassword";

        final SnapConfig snapConfig = new SnapConfig(testUrl, testUser, testPassword);

        final HttpHost expected = new HttpHost("some.site.com", 9123);
        assertThat(snapConfig.getHost(), is(expected));
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldNotAcceptEmptyUrl() {
        final String testUrl = "";
        final String testUser = "testUser";
        final String testPassword = "testPassword";

        final SnapConfig snapConfig = new SnapConfig(testUrl, testUser, testPassword);
    }

    @Test (expected = IllegalArgumentException.class)
    public void shouldNotAcceptInavlidUrl() {
        final String testUrl = "somecheme://not+a domain?query";
        final String testUser = "testUser";
        final String testPassword = "testPassword";

        final SnapConfig snapConfig = new SnapConfig(testUrl, testUser, testPassword);
    }

}