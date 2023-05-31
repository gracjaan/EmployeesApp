package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.resource.contracts.Contract;
import nl.earnit.models.resource.users.UserResponse;

@XmlRootElement
public class WorkedWeekToApproveDTO {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private Boolean confirmed;
    private Boolean approved;
    private Boolean solved;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Company company;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserContract userContract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Contract contract;

    public WorkedWeekToApproveDTO(String id, String contractId, Integer year, Integer week,
                                  String note,
                                  Boolean confirmed, Boolean approved, Boolean solved,
                                  UserResponse user, Company company, UserContract userContract,
                                  Contract contract) {
        this.id = id;
        this.contractId = contractId;
        this.year = year;
        this.week = week;
        this.note = note;
        this.confirmed = confirmed;
        this.approved = approved;
        this.solved = solved;
        this.user = user;
        this.company = company;
        this.userContract = userContract;
        this.contract = contract;
    }

    public WorkedWeekToApproveDTO() {}

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContractId() {
        return contractId;
    }

    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getWeek() {
        return week;
    }

    public void setWeek(Integer week) {
        this.week = week;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public Boolean getConfirmed() {
        return confirmed;
    }

    public void setConfirmed(Boolean confirmed) {
        this.confirmed = confirmed;
    }

    public Boolean getApproved() {
        return approved;
    }

    public void setApproved(Boolean approved) {
        this.approved = approved;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }

    public UserResponse getUser() {
        return user;
    }

    public void setUser(UserResponse user) {
        this.user = user;
    }

    public Company getCompany() {
        return company;
    }

    public void setCompany(Company company) {
        this.company = company;
    }

    public UserContract getUserContract() {
        return userContract;
    }

    public void setUserContract(UserContract userContract) {
        this.userContract = userContract;
    }

    public Contract getContract() {
        return contract;
    }

    public void setContract(Contract contract) {
        this.contract = contract;
    }
}
