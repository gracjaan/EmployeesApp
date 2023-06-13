package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.User;

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

    private List<UserContractDTO> userContracts;

    /**
     * Creates a user model without password, without values.
     */
    public UserDTO() {}

    /**
     * Creates a user model without password from a user model.
     * @param user The user model.
     */
    public UserDTO(User user) {
        this.id = user.getId();
        this.email = user.getEmail();
        this.firstName = user.getFirstName();
        this.lastNamePrefix = user.getLastNamePrefix();
        this.lastName = user.getLastName();
        this.type = user.getType();
    }

    /**
     * Creates a user model without password.
     * @param id The id of the user.
     * @param email The email of the user.
     * @param firstName The first name of the user.
     * @param lastName The last name of the user.
     * @param lastNamePrefix The last name prefix of the user.
     * @param type The type of user: STUDENT, COMPANY, ADMINISTRATOR.
     */
    public UserDTO(String id, String email, String firstName, String lastName, String lastNamePrefix,
                        String type) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.type = type;
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

    public List<UserContractDTO> getUserContracts() {
        return userContracts;
    }

    public void setUserContracts(List<UserContractDTO> userContracts) {
        this.userContracts = userContracts;
    }
}
