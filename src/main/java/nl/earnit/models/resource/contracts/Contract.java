package nl.earnit.models.resource.contracts;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Contract {

    private String id;
    private String role;
    private String description;

    public Contract(){}
    public Contract(String id, String role, String description) {
        this.id = id;
        this.role = role;
        this.description = description;
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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
