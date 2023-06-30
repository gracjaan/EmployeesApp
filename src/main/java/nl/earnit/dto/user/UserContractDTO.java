package nl.earnit.dto.user;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.dto.contracts.ContractDTO;


/**
 * The type User contract dto.
 */
@XmlRootElement
public class UserContractDTO {
    private String id;
    private String contractId;
    private String userId;
    private int hourlyWage;
    private boolean active;
    private ContractDTO contract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponseDTO user;

    /**
     * Instantiates a new User contract dto.
     */
    public UserContractDTO() {}

    /**
     * Instantiates a new User contract dto.
     *
     * @param id         the id
     * @param contractId the contract id
     * @param userId     the user id
     * @param hourlyWage the hourly wage
     * @param active     the active
     */
    public UserContractDTO(String id, String contractId, String userId, int hourlyWage, boolean active) {
        this.id = id;
        this.contractId = contractId;
        this.userId = userId;
        this.hourlyWage = hourlyWage;
        this.active = active;
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
     * Gets contract id.
     *
     * @return the contract id
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * Sets contract id.
     *
     * @param contractId the contract id
     */
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    /**
     * Gets user id.
     *
     * @return the user id
     */
    public String getUserId() {
        return userId;
    }

    /**
     * Sets user id.
     *
     * @param userId the user id
     */
    public void setUserId(String userId) {
        this.userId = userId;
    }

    /**
     * Gets hourly wage.
     *
     * @return the hourly wage
     */
    public int getHourlyWage() {
        return hourlyWage;
    }

    /**
     * Sets hourly wage.
     *
     * @param hourlyWage the hourly wage
     */
    public void setHourlyWage(int hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

    /**
     * Is active boolean.
     *
     * @return the boolean
     */
    public boolean isActive() {
        return active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public UserResponseDTO getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    /**
     * Gets contract.
     *
     * @return the contract
     */
    public ContractDTO getContract() {
        return contract;
    }

    /**
     * Sets contract.
     *
     * @param contract the contract
     */
    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }
}
