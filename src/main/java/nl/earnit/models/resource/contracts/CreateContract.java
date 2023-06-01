package nl.earnit.models.resource.contracts;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateContract {

    private String role;
    private String description;

    public CreateContract(String role, String description) {
        this.role = role;
        this.description = description;
    }

    public CreateContract() {
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
