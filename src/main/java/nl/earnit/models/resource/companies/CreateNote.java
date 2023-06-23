package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateNote {
    private String note;

    public CreateNote() {

    }

    public CreateNote(String note) {
        this.note = note;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }
}
