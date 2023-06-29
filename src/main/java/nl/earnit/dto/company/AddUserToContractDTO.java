package nl.earnit.dto.company;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Add user to contract.
 */
@XmlRootElement
public class AddUserToContractDTO {
    private String userId;
    private int hourlyWage; //keep it in cents


    /**
     * Instantiates a new Add user to contract.
     */
    public AddUserToContractDTO() {
    }

    /**
     * Instantiates a new Add user to contract.
     *
     * @param userId     the user id
     * @param hourlyWage the hourly wage
     */
    public AddUserToContractDTO(String userId, int hourlyWage) {
        this.userId = userId;
        this.hourlyWage = hourlyWage;
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

}
