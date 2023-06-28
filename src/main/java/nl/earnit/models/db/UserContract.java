package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type User contract.
 */
@XmlRootElement
public class UserContract {
    private String id;
    private String contractId;
    private String userId;
    private int hourlyWage;
    private boolean active;

    /**
     * Instantiates a new User contract.
     */
    public UserContract() {}

    /**
     * Instantiates a new User contract.
     *
     * @param id         the id
     * @param contractId the contract id
     * @param userId     the user id
     * @param hourlyWage the hourly wage
     * @param active     the active
     */
    public UserContract(String id, String contractId, String userId, int hourlyWage, boolean active) {
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
}
