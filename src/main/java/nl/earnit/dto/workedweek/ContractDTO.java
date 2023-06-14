package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.Company;

import java.util.List;

@XmlRootElement
public class ContractDTO {

    private String id;
    private String role;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Company company;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserContractDTO> userContracts;

    public ContractDTO(){}
    public ContractDTO(String id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public List<UserContractDTO> getUserContracts() {
        return userContracts;
    }

    public void setUserContracts(List<UserContractDTO> userContracts) {
        this.userContracts = userContracts;
    }
}
