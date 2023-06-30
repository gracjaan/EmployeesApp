package nl.earnit.dto.contracts;

/**
 * The type Description role.
 */
public class ContractInformationDTO {


    private String role;

    private String description;

    /**
     * Instantiates a new Description role.
     */
    public ContractInformationDTO() {
    }

    /**
     * Instantiates a new Description role.
     *
     * @param role        the role
     * @param description the description
     */
    public ContractInformationDTO(String role, String description) {
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
}
