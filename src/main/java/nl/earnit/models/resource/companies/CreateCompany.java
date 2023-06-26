package nl.earnit.models.resource.companies;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateCompany {
    private String name;
    private String userId;
    private String kvk;
    private String address;


    public CreateCompany() {

    }

    public CreateCompany(String name, String userId, String kvk, String address) {
        this.name = name;
        this.userId = userId;
        this.kvk = kvk;
        this.address = address;
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

    public String getKvk() {
        return kvk;
    }

    public void setKvk(String kvk) {
        this.kvk = kvk;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
