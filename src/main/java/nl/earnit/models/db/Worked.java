package nl.earnit.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Worked {
    private String id;
    private String workedWeekId;
    private int day;
    private int minutes;
    private String work;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer suggestion;

    public Worked() {

    }

    public Worked(String id, String workedWeekId, int day, int minutes, String work) {
        this.id = id;
        this.workedWeekId = workedWeekId;
        this.day = day;
        this.minutes = minutes;
        this.work = work;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getWorkedWeekId() {
        return workedWeekId;
    }

    public void setWorkedWeekId(String workedWeekId) {
        this.workedWeekId = workedWeekId;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMinutes() {
        return minutes;
    }

    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public Integer getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }
}
