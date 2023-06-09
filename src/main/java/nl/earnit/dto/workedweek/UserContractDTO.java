package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.resource.users.UserResponse;

@XmlRootElement
public class UserContractDTO {
    private String id;
    private String contractId;
    private String userId;
    private int hourlyWage;
    private boolean active;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ContractDTO contract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;

    public UserContractDTO() {}

    public UserContractDTO(String id, String contractId, String userId, int hourlyWage, boolean active) {
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

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public ContractDTO getContract() {
        return contract;
    }

    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }
}
