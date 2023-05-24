package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkedWeek {
    private String id;
    private String contractId;
    private String year;
    private String week;
    private String note;
    private boolean confirmed;
    private boolean approved;
    private boolean solved;

    public WorkedWeek() {

    }

    public WorkedWeek(String id, String contractId,String year, String week, String note, boolean confirmed, boolean approved, boolean solved) {
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

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getWeek() {
        return week;
    }

    public void setWeek(String week) {
        this.week = week;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public boolean isSolved() {
        return solved;
    }

    public void setSolved(boolean solved) {
        this.solved = solved;
    }
}
