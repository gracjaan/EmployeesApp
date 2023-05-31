package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkedWeek {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private Boolean confirmed;
    private Boolean approved;
    private Boolean solved;

    public WorkedWeek() {}

    public WorkedWeek(String id, String contractId, Integer year, Integer week, String note,
                      Boolean confirmed, Boolean approved, Boolean solved) {
        this.id = id;
        this.contractId = contractId;
        this.year = year;
        this.week = week;
        this.note = note;
        this.confirmed = confirmed;
        this.approved = approved;
        this.solved = solved;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }

    @Override
    public String toString() {
        return "WorkedWeek{" +
            "id='" + id + '\'' +
            ", contractId='" + contractId + '\'' +
            ", year=" + year +
            ", week=" + week +
            ", note='" + note + '\'' +
            ", confirmed=" + confirmed +
            ", approved=" + approved +
            ", solved=" + solved +
            '}';
    }
}
