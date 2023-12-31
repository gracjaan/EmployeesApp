package nl.earnit.dto.user;

import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.User;

import java.util.List;

/**
 * User model without a password.
 */
@XmlRootElement
public class UserDTO {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String lastNamePrefix;
    private String type;
    private String kvk;
    private String btw;
    private String address;

    private List<UserContractDTO> userContracts;

    /**
     * Creates a user model without password, without values.
     */
    public UserDTO() {}

    /**
     * Creates a user model without password from a user model.
     *
     * @param user The user model.
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail().toLowerCase();
        this.firstName = user.getFirstName();
        this.lastNamePrefix = user.getLastNamePrefix();
        this.lastName = user.getLastName();
        this.type = user.getType();
    }

    /**
     * Creates a user model without password.
     *
     * @param id             The id of the user.
     * @param email          The email of the user.
     * @param firstName      The first name of the user.
     * @param lastName       The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param type           The type of user: STUDENT, COMPANY, ADMINISTRATOR.
     * @param kvk            the kvk
     * @param btw            the btw
     * @param address        the address
     */
    public UserDTO(String id, String email, String firstName, String lastName, String lastNamePrefix,
                        String type, String kvk, String btw, String address) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.type = type;
        this.address = address;
        this.kvk = kvk;
        this.btw = btw;
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

    /**
     * Gets user contracts.
     *
     * @return the user contracts
     */
    public List<UserContractDTO> getUserContracts() {
        return userContracts;
    }

    /**
     * Sets user contracts.
     *
     * @param userContracts the user contracts
     */
    public void setUserContracts(List<UserContractDTO> userContracts) {
        this.userContracts = userContracts;
    }
}
