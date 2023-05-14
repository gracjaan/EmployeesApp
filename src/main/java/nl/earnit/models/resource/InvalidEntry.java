package nl.earnit.models.resource;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class InvalidEntry {
    private String field;

    public InvalidEntry() {}

    public InvalidEntry(String field) {
        this.field = field;
    }

    public String getField() {
        return field;
    }

    public void setField(String field) {
        this.field = field;
    }
}
