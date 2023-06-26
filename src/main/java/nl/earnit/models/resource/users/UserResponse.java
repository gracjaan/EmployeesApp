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
    private String kvk;
    private String btw;
    private String address;

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
        this.btw = user.getBtw();
        this.kvk = user.getKvk();
        this.address = user.getAddress();
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
                        String type, String kvk, String btw, String address) {
        this.id = id;
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.lastNamePrefix = lastNamePrefix;
        this.type = type;
        this.kvk = kvk;
        this.btw = btw;
        this.address = address;
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
