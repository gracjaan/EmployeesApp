package nl.earnit.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Company {
    private String id;
    private String name;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;

    public Company() {}

    public Company(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getActive() {
        return active;
    }

    public void setActive(Boolean active) {
        this.active = active;
    }
}
