package nl.earnit.models.resource.users;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * User model for the user POST request.
 */
@XmlRootElement
public class CreateUser {
    private String email;
    private String firstName;
    private String lastName;
    private String lastNamePrefix;
    private String password;
    private String kvk;
    private String btw;
    private String address;

    /**
     * Creates a user model for the user POST request.
     */
    public CreateUser() {}

    /**
     * Creates a user model for the user POST request.
     *
     * @param email          The email of the user.
     * @param firstName      The first name of the user.
     * @param lastName       The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param password       The password of the user.
     * @param kvk            the kvk
     * @param btw            the btw
     * @param address        the address
     */
    public CreateUser(String email, String firstName, String lastName, String lastNamePrefix,
                      String password, String kvk, String btw, String address) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.password = password;
        this.kvk = kvk;
        this.btw = btw;
        this.address = address;
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
