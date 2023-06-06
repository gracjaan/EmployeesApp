package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.dao.UserContractDAO;
import nl.earnit.models.resource.contracts.Contract;

@XmlRootElement
public class UserContractDTO {
    private String id;
    private String contractId;
    private String userId;
    private int hourlyWage;
    private boolean active;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Contract contract;

    public UserContractDTO() {}
    public UserContractDTO(String id, String contractId, String userId, int hourlyWage, boolean active, Contract contract) {
        this.id = id;
        this.contractId = contractId;
        this.userId = userId;
        this.hourlyWage = hourlyWage;
        this.active = active;
        this.contract = contract;
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(int hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}
