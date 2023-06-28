package nl.earnit.models.resource.contracts;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Contract.
 */
@XmlRootElement
public class Contract {

    private String id;
    private String role;
    private String description;

    /**
     * Instantiates a new Contract.
     */
    public Contract(){}

    /**
     * Instantiates a new Contract.
     *
     * @param id          the id
     * @param role        the role
     * @param description the description
     */
    public Contract(String id, String role, String description) {
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
}
