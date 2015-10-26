package kuona.snapci.analyser.model;

import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Stage {
    /*
        Capture groups
        #0	ThoughtWorksInc/Palace-Intrigue (master) :: GULP-Build
        #1	ThoughtWorksInc
        #2	Palace-Intrigue
        #3	master
        #4	 GULP-Build
    */
    private final static Pattern projectName = Pattern.compile("^(\\w+)\\/([\\w\\s-]+)\\((\\w+)\\)\\s::([\\w\\s-]+)$");

    private int counter;
    private String name;
    private String full_name;
    private String result;
    private int duration;
    private Date started_at;
    private Date completed_at;


    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter = counter;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }

    public String getFull_name() {
        return full_name;
    }

    public void setFull_name(String full_name) {
        this.full_name = full_name;
    }

    public Date getStarted_at() {
        return started_at;
    }

    public void setStarted_at(Date started_at) {
        this.started_at = started_at;
    }

    public Date getCompleted_at() {
        return completed_at;
    }

    public void setCompleted_at(Date completed_at) {
        this.completed_at = completed_at;
    }

    public String getProjectName() {
        Matcher matcher = projectName.matcher(full_name);
        if (matcher.matches()) {
            return matcher.group(2).trim();
        }
        else return "";
    }
}
