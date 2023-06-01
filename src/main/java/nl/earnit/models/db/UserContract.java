package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserContract {
    private String id;
    private String contractId;
    private String userId;
    private Integer hourlyWage;
    private boolean active;

    public UserContract() {}

    public UserContract(String id, String contractId, String userId, Integer hourlyWage, boolean active) {
        this.id = id;
        this.contractId = contractId;
        this.userId = userId;
        this.hourlyWage = hourlyWage;
        this.active = active;
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

    public Integer getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(Integer hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
