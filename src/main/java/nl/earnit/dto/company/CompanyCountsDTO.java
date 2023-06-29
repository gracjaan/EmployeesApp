package nl.earnit.dto.company;

/**
 * The type Company counts.
 */
public class CompanyCountsDTO {
    private String id;
    private String name;
    private int count;


    /**
     * Instantiates a new Company counts.
     *
     * @param id    the id
     * @param name  the name
     * @param count the count
     */
    public CompanyCountsDTO(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    /**
     * Instantiates a new Company counts.
     */
    public CompanyCountsDTO() {

    }

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
     * Gets name.
     *
     * @return the name
     */
    public String getName() {
        return name;
    }

    /**
     * Sets name.
     *
     * @param name the name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets count.
     *
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Sets count.
     *
     * @param count the count
     */
    public void setCount(int count) {
        this.count = count;
    }

}
