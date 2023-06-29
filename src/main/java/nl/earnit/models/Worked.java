package nl.earnit.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Worked.
 */
@XmlRootElement
public class Worked {
    private String id;
    private String workedWeekId;
    private int day;
    private int minutes;
    private String work;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer suggestion;

    /**
     * Instantiates a new Worked.
     */
    public Worked() {

    }

    /**
     * Instantiates a new Worked.
     *
     * @param id           the id
     * @param workedWeekId the worked week id
     * @param day          the day
     * @param minutes      the minutes
     * @param work         the work
     */
    public Worked(String id, String workedWeekId, int day, int minutes, String work) {
        this.id = id;
        this.workedWeekId = workedWeekId;
        this.day = day;
        this.minutes = minutes;
        this.work = work;
    }

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets worked week id.
     *
     * @return the worked week id
     */
    public String getWorkedWeekId() {
        return workedWeekId;
    }

    /**
     * Sets worked week id.
     *
     * @param workedWeekId the worked week id
     */
    public void setWorkedWeekId(String workedWeekId) {
        this.workedWeekId = workedWeekId;
    }

    /**
     * Gets day.
     *
     * @return the day
     */
    public int getDay() {
        return day;
    }

    /**
     * Sets day.
     *
     * @param day the day
     */
    public void setDay(int day) {
        this.day = day;
    }

    /**
     * Gets minutes.
     *
     * @return the minutes
     */
    public int getMinutes() {
        return minutes;
    }

    /**
     * Sets minutes.
     *
     * @param minutes the minutes
     */
    public void setMinutes(int minutes) {
        this.minutes = minutes;
    }

    /**
     * Gets work.
     *
     * @return the work
     */
    public String getWork() {
        return work;
    }

    /**
     * Sets work.
     *
     * @param work the work
     */
    public void setWork(String work) {
        this.work = work;
    }

    /**
     * Gets suggestion.
     *
     * @return the suggestion
     */
    public Integer getSuggestion() {
        return suggestion;
    }

    /**
     * Sets suggestion.
     *
     * @param suggestion the suggestion
     */
    public void setSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }
}
