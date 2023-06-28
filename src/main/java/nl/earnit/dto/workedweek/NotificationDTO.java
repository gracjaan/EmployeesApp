package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotificationDTO {
    private String id;
    private String date;
    private boolean seen;
    private String title;
    private String description;
    private String userId;
    private String companyId;
    private String userContractId;
    private String contractId;
    private String workedWeekId;
    private int week;
    private String type;

    public NotificationDTO(String id, String date, boolean seen, String type, String title, String description, String userId, String companyId, String userContractId, String contractId, String workedWeekId, int week) {
        this.id  = id;
        this.date = date;
        this.seen = seen;
        this.type = type;
        this.title = title;
        this.description = description;
        this.userId = userId;
        this.companyId = companyId;
        this.userContractId = userContractId;
        this.contractId = contractId;
        this.workedWeekId = workedWeekId;
        this.week = week;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public String getUserContractId() {
        return userContractId;
    }

    public void setUserContractId(String userContractId) {
        this.userContractId = userContractId;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public String getWorkedWeekId() {
        return workedWeekId;
    }

    public void setWorkedWeekId(String workedWeekId) {
        this.workedWeekId = workedWeekId;
    }

    public int getWeek() {
        return week;
    }

    public void setWeek(int week) {
        this.week = week;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
