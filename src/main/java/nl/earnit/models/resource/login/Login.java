package nl.earnit.models.resource.login;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Login.
 */
@XmlRootElement
public class Login {
    private String email;
    private String password;
    private String companyId;

    /**
     * Instantiates a new Login.
     */
    public Login() {

    }

    /**
     * Instantiates a new Login.
     *
     * @param email     the email
     * @param password  the password
     * @param companyId the company id
     */
    public Login(String email, String password, String companyId) {
        this.email = email;
        this.password = password;
        this.companyId = companyId;
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
     * Gets company id.
     *
     * @return the company id
     */
    public String getCompanyId() {
        return companyId;
    }

    /**
     * Sets company id.
     *
     * @param companyId the company id
     */
    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
