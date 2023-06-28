package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.Company;

import java.util.List;

/**
 * The type Contract dto.
 */
@XmlRootElement
public class ContractDTO {

    private String id;
    private String role;
    private String description;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Company company;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<UserContractDTO> userContracts;

    /**
     * Instantiates a new Contract dto.
     */
    public ContractDTO(){}

    /**
     * Instantiates a new Contract dto.
     *
     * @param id          the id
     * @param role        the role
     * @param description the description
     */
    public ContractDTO(String id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
    }

    /**
     * Gets role.
     *
     * @return the role
     */
    public String getRole() {
        return role;
    }

    /**
     * Sets role.
     *
     * @param role the role
     */
    public void setRole(String role) {
        this.role = role;
    }

    /**
     * Gets description.
     *
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Sets description.
     *
     * @param description the description
     */
    public void setDescription(String description) {
        this.description = description;
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
     * Gets company.
     *
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Sets company.
     *
     * @param company the company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * Gets user contracts.
     *
     * @return the user contracts
     */
    public List<UserContractDTO> getUserContracts() {
        return userContracts;
    }

    /**
     * Sets user contracts.
     *
     * @param userContracts the user contracts
     */
    public void setUserContracts(List<UserContractDTO> userContracts) {
        this.userContracts = userContracts;
    }
}
