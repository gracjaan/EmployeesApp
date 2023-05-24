package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class worked_week {
    private int id;
    private int contractId;
    private int week;
    private String note;
    private boolean confirmed;
    private boolean approved;
    private boolean solved;

    public worked_week(int id, int contractId, int week, String note, boolean confirmed, boolean approved, boolean solved) {
        this.id = id;
        this.contractId = contractId;
        this.week = week;
        this.note = note;
        this.confirmed = confirmed;
        this.approved = approved;
        this.solved = solved;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getContractId() {
        return contractId;
    }

    public void setContractId(int contractId) {
        this.contractId = contractId;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
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
