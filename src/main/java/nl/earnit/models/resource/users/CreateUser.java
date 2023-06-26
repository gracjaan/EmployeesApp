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
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param password The password of the user.
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

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getLastNamePrefix() {
        return lastNamePrefix;
    }

    public void setLastNamePrefix(String lastNamePrefix) {
        this.lastNamePrefix = lastNamePrefix;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getKvk() {
        return kvk;
    }

    public void setKvk(String kvk) {
        this.kvk = kvk;
    }

    public String getBtw() {
        return btw;
    }

    public void setBtw(String btw) {
        this.btw = btw;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
