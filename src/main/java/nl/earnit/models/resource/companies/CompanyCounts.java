package nl.earnit.models.resource.companies;

public class CompanyCounts {
    private String id;
    private String name;
    private int count;


    public CompanyCounts(String id, String name, int count) {
        this.id = id;
        this.name = name;
        this.count = count;
    }

    public CompanyCounts() {

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

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

}
