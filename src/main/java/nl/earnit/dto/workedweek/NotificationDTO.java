package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

import java.util.Date;

@XmlRootElement
public class NotificationDTO {
    private String id;
    private Date date;
    private boolean seen;
    private String message;

    public NotificationDTO(String id, Date date, boolean seen, String message) {
        this.id  = id;
        this.date = date;
        this.seen = seen;
        this.message = message;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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
