package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateCompany {
    private String name;
    private String userId;

    public CreateCompany() {}

    public CreateCompany(String name, String userId) {
        this.name = name;
        this.userId = userId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
