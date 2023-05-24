package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Worked {
    private int id;
    private int workedWeekId;
    private int day;
    private int minutes;
    private String work;

    public Worked() {

    }

    public Worked(int id, int workedWeekId, int day, int minutes, String work) {
        this.id = id;
        this.workedWeekId = workedWeekId;
        this.day = day;
        this.minutes = minutes;
        this.work = work;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getWorkedWeekId() {
        return workedWeekId;
    }

    public void setWorkedWeekId(int workedWeekId) {
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
}
