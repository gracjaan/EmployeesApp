package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class NotificationDTO {
    private String id;
    private String date;
    private boolean seen;
    private String title;
    private String description;

    public NotificationDTO(String id, String date, boolean seen, String title, String description) {
        this.id  = id;
        this.date = date;
        this.seen = seen;
        this.title = title;
        this.description = description;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public boolean isSeen() {
        return seen;
    }

    public void setSeen(boolean seen) {
        this.seen = seen;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
