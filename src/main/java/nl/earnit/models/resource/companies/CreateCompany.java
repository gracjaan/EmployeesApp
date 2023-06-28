package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Create company.
 */
@XmlRootElement
public class CreateCompany {
    private String name;
    private String userId;
    private String kvk;
    private String address;


    /**
     * Instantiates a new Create company.
     */
    public CreateCompany() {

    }

    /**
     * Instantiates a new Create company.
     *
     * @param name    the name
     * @param userId  the user id
     * @param kvk     the kvk
     * @param address the address
     */
    public CreateCompany(String name, String userId, String kvk, String address) {
        this.name = name;
        this.userId = userId;
        this.kvk = kvk;
        this.address = address;
    }

    /**
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
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
     * Gets kvk.
     *
     * @return the kvk
     */
    public String getKvk() {
        return kvk;
    }

    /**
     * Sets kvk.
     *
     * @param kvk the kvk
     */
    public void setKvk(String kvk) {
        this.kvk = kvk;
    }

    /**
     * Gets address.
     *
     * @return the address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Sets address.
     *
     * @param address the address
     */
    public void setAddress(String address) {
        this.address = address;
    }
}
