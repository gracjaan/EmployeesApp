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
     * @param id The id of the user.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param type The type of user: STUDENT, COMPANY, ADMINISTRATOR.
     * @param password The hashed password of the user.
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
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

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getBtw() {
        return btw;
    }

    public void setBtw(String btw) {
        this.btw = btw;
    }

    public enum Type {
        ADMINISTRATOR, STUDENT, COMPANY
    }
}