package nl.earnit.models.db;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class Company {
    private String id;
    private String name;
    private String kvk;
    private String address;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Boolean active;


    public Company() {}

    public Company(String id, String name, String kvk, String address) {
        this.id = id;
        this.name = name;
        this.kvk = kvk;
        this.address = address;
    }

    public String getId() {
        return id;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getKvk() {
        return kvk;
    }

    public void setKvk(String kvk) {
        this.kvk = kvk;
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
