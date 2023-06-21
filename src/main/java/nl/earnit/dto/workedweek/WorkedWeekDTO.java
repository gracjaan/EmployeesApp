package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.models.db.Company;
import nl.earnit.models.db.UserContract;
import nl.earnit.models.db.Worked;
import nl.earnit.models.resource.contracts.Contract;
import nl.earnit.models.resource.users.UserResponse;

import java.util.List;

@XmlRootElement
public class WorkedWeekDTO {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponse user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Company company;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserContract userContract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Contract contract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Worked> hours;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalMinutes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String companyNote;

    public WorkedWeekDTO(String id, String contractId, Integer year, Integer week,
                         String note,
                         String status,
                         UserResponse user, Company company, UserContract userContract,
                         Contract contract, List<Worked> hours, Integer totalHours) {
        this.id = id;
        this.contractId = contractId;
        this.year = year;
        this.week = week;
        this.note = note;
        this.status = status;
        this.user = user;
        this.company = company;
        this.userContract = userContract;
        this.contract = contract;
        this.hours = hours;
        this.totalMinutes = totalHours;
    }

    public WorkedWeekDTO() {}

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
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

    public List<Worked> getHours() {
        return hours;
    }

    public void setHours(List<Worked> hours) {
        this.hours = hours;
    }

    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    public String getCompanyNote() {
        return companyNote;
    }

    public void setCompanyNote(String companyNote) {
        this.companyNote = companyNote;
    }
}
