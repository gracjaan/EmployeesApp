package nl.earnit.models;

import java.util.Date;

public class Notification {
    private String id;
    private String user_id;
    private String company_id;
    private Date date;
    private boolean seen;
    private String type;

    public Notification(String id, String user_id, String company_id, Date date, boolean seen, String type) {
        this.id = id;
        this.user_id = user_id;
        this.company_id = company_id;
        this.date = date;
        this.seen = seen;
        this.type = type;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getCompany_id() {
        return company_id;
    }

    public void setCompany_id(String company_id) {
        this.company_id = company_id;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getType() {
        return type;
    }

    public void setType(String message) {
        this.type = message;
    }
}
