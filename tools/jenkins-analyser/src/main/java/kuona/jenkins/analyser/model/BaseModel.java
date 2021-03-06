/*
 * Copyright (c) 2013 Rising Oak LLC.
 *
 * Distributed under the MIT license: http://opensource.org/licenses/MIT
 */

package kuona.jenkins.analyser.model;


import kuona.jenkins.analyser.JenkinsClient;

public class BaseModel {
    JenkinsClient client;

    public JenkinsClient getClient() {
        return client;
    }

    public void setClient(JenkinsClient client) {
        this.client = client;
    }
}
