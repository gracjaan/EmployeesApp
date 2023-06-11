package nl.earnit.models.resource.login;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Login {
    private String email;
    private String password;
    private String companyId;

    public Login() {

    }

    public Login(String email, String password, String companyId) {
        this.email = email;
        this.password = password;
        this.companyId = companyId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }
}
