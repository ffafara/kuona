/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.model;

import java.io.IOException;

public class Job extends BaseModel {
    private String name;
    private String url;

    public Job() {
    }

    public Job(String name, String url) {
        this();
        this.name = name;
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public String getUrl() {
        return url;
    }

    public JobWithDetails details() throws IOException {
        return client.get(url, JobWithDetails.class);
    }

    public String detailsJson() throws IOException {
        return client.get(url);
    }

    @Override
    public int hashCode() {
        return 342 + name.hashCode() + url.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        Job other = (Job) obj;
        return name.equals(other.name) && url.equals(other.url);
    }
}
