package kuona.web.model;

import lombok.Getter;
import org.elasticsearch.common.joda.time.DateTime;
import org.elasticsearch.common.joda.time.LocalTime;

import java.sql.Time;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.util.Date;
import java.util.Map;

public class Project {
    enum ProjectStatus {
        Active,
        Inactive
    }
    @Getter
    private final ProjectStatus status;
    @Getter
    private final Date created;
    @Getter
    String name;
    @Getter
    String description;
    @Getter
    int completion;

    public Project(String name, String description) {
        this();
        this.name = name;
        this.description = description;
    }

    public Project() {
        created = new Date();
        completion = 20;
        status = ProjectStatus.Active;
    }

    public static Project fromMap(Map<String, String[]> values) {
        final Project project = new Project();
        project.name = values.get("name")[0];
        project.description = values.get("description")[0];
        return project;
    }
}
