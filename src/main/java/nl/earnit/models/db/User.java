package nl.earnit.models.db;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * User model.
 */
@XmlRootElement
public class User {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String lastNamePrefix;
    private String type;
    private String password;
    private String kvk;
    private String address;
    private String btw;

    /**
     * Creates a user model without values.
     */
    public User() {}

    /**
     * Creates a user model.
     *
     * @param id             The id of the user.
     * @param email          The email of the user.
     * @param firstName      The first name of the user.
     * @param lastName       The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param type           The type of user: STUDENT, COMPANY, ADMINISTRATOR.
     * @param password       The hashed password of the user.
     * @param address        the address
     * @param btw            the btw
     * @param kvk            the kvk
     */
    public User(String id, String email, String firstName, String lastName, String lastNamePrefix,
                String type, String password, String address, String btw, String kvk) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.type = type;
        this.password = password;
        this.address = address;
        this.btw = btw;
        this.kvk = kvk;
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
     * Gets email.
     *
     * @return the email
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets email.
     *
     * @param email the email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Gets first name.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Sets first name.
     *
     * @param firstName the first name
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * Gets last name.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Sets last name.
     *
     * @param lastName the last name
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * Gets last name prefix.
     *
     * @return the last name prefix
     */
    public String getLastNamePrefix() {
        return lastNamePrefix;
    }

    /**
     * Sets last name prefix.
     *
     * @param lastNamePrefix the last name prefix
     */
    public void setLastNamePrefix(String lastNamePrefix) {
        this.lastNamePrefix = lastNamePrefix;
    }

    /**
     * Gets type.
     *
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * Sets type.
     *
     * @param type the type
     */
    public void setType(String type) {
        this.type = type;
    }

    /**
     * Gets password.
     *
     * @return the password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Sets password.
     *
     * @param password the password
     */
    public void setPassword(String password) {
        this.password = password;
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

    /**
     * Gets btw.
     *
     * @return the btw
     */
    public String getBtw() {
        return btw;
    }

    /**
     * Sets btw.
     *
     * @param btw the btw
     */
    public void setBtw(String btw) {
        this.btw = btw;
    }

    /**
     * The enum Type.
     */
    public enum Type {
        /**
         * Administrator type.
         */
        ADMINISTRATOR,
        /**
         * Student type.
         */
        STUDENT,
        /**
         * Company type.
         */
        COMPANY
    }
}