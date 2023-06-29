package nl.earnit.models;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Company.
 */
@XmlRootElement
public class Company {
    private String id;
    private String name;
    private String kvk;
    private String address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;


    /**
     * Instantiates a new Company.
     */
    public Company() {}

    /**
     * Instantiates a new Company.
     *
     * @param id      the id
     * @param name    the name
     * @param kvk     the kvk
     * @param address the address
     */
    public Company(String id, String name, String kvk, String address) {
        this.id = id;
        this.name = name;
        this.kvk = kvk;
        this.address = address;
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
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
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
     * Gets active.
     *
     * @return the active
     */
    public Boolean getActive() {
        return active;
    }

    /**
     * Sets active.
     *
     * @param active the active
     */
    public void setActive(Boolean active) {
        this.active = active;
    }
}
