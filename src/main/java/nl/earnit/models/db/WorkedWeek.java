package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkedWeek {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private String status;

    public WorkedWeek() {}

    public WorkedWeek(String id, String contractId, Integer year, Integer week, String note,
                      String status) {
        this.id = id;
        this.contractId = contractId;
        this.year = year;
        this.week = week;
        this.note = note;
        this.status = status;
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

    public String getStatus() {
        return status;
    }

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
