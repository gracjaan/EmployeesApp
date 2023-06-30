package nl.earnit.models;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Worked week.
 */
@XmlRootElement
public class WorkedWeek {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private String status;

    /**
     * Instantiates a new Worked week.
     */
    public WorkedWeek() {}

    /**
     * Instantiates a new Worked week.
     *
     * @param id         the id
     * @param contractId the contract id
     * @param year       the year
     * @param week       the week
     * @param note       the note
     * @param status     the status
     */
    public WorkedWeek(String id, String contractId, Integer year, Integer week, String note,
                      String status) {
        this.id = id;
        this.contractId = contractId;
        this.year = year;
        this.week = week;
        this.note = note;
        this.status = status;
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
     * Gets contract id.
     *
     * @return the contract id
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * Sets contract id.
     *
     * @param contractId the contract id
     */
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets year.
     *
     * @param year the year
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets week.
     *
     * @return the week
     */
    public Integer getWeek() {
        return week;
    }

    /**
     * Sets week.
     *
     * @param week the week
     */
    public void setWeek(Integer week) {
        this.week = week;
    }

    /**
     * Gets note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets note.
     *
     * @param note the note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "WorkedWeek{" +
            "id='" + id + '\'' +
            ", contractId='" + contractId + '\'' +
            ", year=" + year +
            ", week=" + week +
            ", note='" + note + '\'' +
            ", status=" + status +
            '}';
    }
}
