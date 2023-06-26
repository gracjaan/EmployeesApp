package nl.earnit.dto.workedweek;

import java.util.Date;

public class NotificationDTO {
    private String user_name;
    private String company_name;
    private Date date;
    private boolean seen;
    private String message;

    public NotificationDTO(String user_name, String company_name, Date date, boolean seen, String message) {
        // either user_name or company_name should be null
        this.user_name = user_name;
        this.company_name = company_name;
        this.date = date;
        this.seen = seen;
        this.message = message;
    }

    public NotificationDTO(String company_name, Date date, boolean seen, String message) {
        this.user_name = null;
        this.company_name = company_name;
        this.date = date;
        this.seen = seen;
        this.message = message;
    }

    public String getUser_name() {
        return user_name;
    }

    public void setUser_name(String user_name) {
        this.user_name = user_name;
    }

    public String getCompany_name() {
        return company_name;
    }

    public void setCompany_name(String company_name) {
        this.company_name = company_name;
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

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
