package nl.earnit.dto.workedweek;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.xml.bind.annotation.XmlRootElement;
import nl.earnit.dto.contracts.ContractDTO;
import nl.earnit.models.Company;
import nl.earnit.models.UserContract;
import nl.earnit.models.Worked;
import nl.earnit.dto.user.UserResponseDTO;

import java.util.List;

/**
 * The type Worked week dto.
 */
@XmlRootElement
public class WorkedWeekDTO {
    private String id;
    private String contractId;
    private Integer year;
    private Integer week;
    private String note;
    private String status;

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserResponseDTO user;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Company company;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private UserContract userContract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private ContractDTO contract;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private List<Worked> hours;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Integer totalMinutes;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String companyNote;

    /**
     * Instantiates a new Worked week dto.
     *
     * @param id           the id
     * @param contractId   the contract id
     * @param year         the year
     * @param week         the week
     * @param note         the note
     * @param status       the status
     * @param user         the user
     * @param company      the company
     * @param userContract the user contract
     * @param contract     the contract
     * @param hours        the hours
     * @param totalHours   the total hours
     */
    public WorkedWeekDTO(String id, String contractId, Integer year, Integer week,
                         String note,
                         String status,
                         UserResponseDTO user, Company company, UserContract userContract,
                         ContractDTO contract, List<Worked> hours, Integer totalHours) {
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

    /**
     * Instantiates a new Worked week dto.
     */
    public WorkedWeekDTO() {}

    /**
     * Gets id.
     *
     * @return the id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets id.
     *
     * @param id the id
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets contract id.
     *
     * @return the contract id
     */
    public String getContractId() {
        return contractId;
    }

    /**
     * Sets contract id.
     *
     * @param contractId the contract id
     */
    public void setContractId(String contractId) {
        this.contractId = contractId;
    }

    /**
     * Gets year.
     *
     * @return the year
     */
    public Integer getYear() {
        return year;
    }

    /**
     * Sets year.
     *
     * @param year the year
     */
    public void setYear(Integer year) {
        this.year = year;
    }

    /**
     * Gets week.
     *
     * @return the week
     */
    public Integer getWeek() {
        return week;
    }

    /**
     * Sets week.
     *
     * @param week the week
     */
    public void setWeek(Integer week) {
        this.week = week;
    }

    /**
     * Gets note.
     *
     * @return the note
     */
    public String getNote() {
        return note;
    }

    /**
     * Sets note.
     *
     * @param note the note
     */
    public void setNote(String note) {
        this.note = note;
    }

    /**
     * Gets status.
     *
     * @return the status
     */
    public String getStatus() {
        return status;
    }

    /**
     * Sets status.
     *
     * @param status the status
     */
    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * Gets user.
     *
     * @return the user
     */
    public UserResponseDTO getUser() {
        return user;
    }

    /**
     * Sets user.
     *
     * @param user the user
     */
    public void setUser(UserResponseDTO user) {
        this.user = user;
    }

    /**
     * Gets company.
     *
     * @return the company
     */
    public Company getCompany() {
        return company;
    }

    /**
     * Sets company.
     *
     * @param company the company
     */
    public void setCompany(Company company) {
        this.company = company;
    }

    /**
     * Gets user contract.
     *
     * @return the user contract
     */
    public UserContract getUserContract() {
        return userContract;
    }

    /**
     * Sets user contract.
     *
     * @param userContract the user contract
     */
    public void setUserContract(UserContract userContract) {
        this.userContract = userContract;
    }

    /**
     * Gets contract.
     *
     * @return the contract
     */
    public ContractDTO getContract() {
        return contract;
    }

    /**
     * Sets contract.
     *
     * @param contract the contract
     */
    public void setContract(ContractDTO contract) {
        this.contract = contract;
    }

    /**
     * Gets hours.
     *
     * @return the hours
     */
    public List<Worked> getHours() {
        return hours;
    }

    /**
     * Sets hours.
     *
     * @param hours the hours
     */
    public void setHours(List<Worked> hours) {
        this.hours = hours;
    }

    /**
     * Gets total minutes.
     *
     * @return the total minutes
     */
    public Integer getTotalMinutes() {
        return totalMinutes;
    }

    /**
     * Sets total minutes.
     *
     * @param totalMinutes the total minutes
     */
    public void setTotalMinutes(Integer totalMinutes) {
        this.totalMinutes = totalMinutes;
    }

    /**
     * Gets company note.
     *
     * @return the company note
     */
    public String getCompanyNote() {
        return companyNote;
    }

    /**
     * Sets company note.
     *
     * @param companyNote the company note
     */
    public void setCompanyNote(String companyNote) {
        this.companyNote = companyNote;
    }
}
