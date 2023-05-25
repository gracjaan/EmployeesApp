package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class AddUserToContract {
    private String userId;
    private int hourlyWage; //keep it in cents



    public AddUserToContract() {
    }

    public AddUserToContract(String userId, int hourlyWage) {
        this.userId = userId;
        this.hourlyWage = hourlyWage;
    }


    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public int getHourlyWage() {
        return hourlyWage;
    }

    public void setHourlyWage(int hourlyWage) {
        this.hourlyWage = hourlyWage;
    }

}
