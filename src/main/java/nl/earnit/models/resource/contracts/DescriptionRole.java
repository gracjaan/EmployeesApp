package nl.earnit.models.resource.contracts;

public class DescriptionRole {


    private String role;

    private String description;

    public DescriptionRole() {
    }

    public DescriptionRole(String role, String description) {
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
}
