package nl.earnit.models.resource.users;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.User;

/**
 * User model without a password.
 */
@XmlRootElement
public class UserResponse {
    private String id;
    private String email;
    private String firstName;
    private String lastName;
    private String lastNamePrefix;
    private String type;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;

    /**
     * Creates a user model without password, without values.
     */
    public UserResponse() {}

    /**
     * Creates a user model without password from a user model.
     * @param user The user model.
     */
    public UserResponse(User user) {
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
    public UserResponse(String id, String email, String firstName, String lastName, String lastNamePrefix,
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

    public void setActive(Boolean active){
        this.active = active;
    }

    public Boolean getActive(){
        return this.active;
    }
}
