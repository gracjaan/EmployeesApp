package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Create note.
 */
@XmlRootElement
public class CreateNote {
    private String note;

    /**
     * Instantiates a new Create note.
     */
    public CreateNote() {

    }

    /**
     * Instantiates a new Create note.
     *
     * @param note the note
     */
    public CreateNote(String note) {
        this.note = note;
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
}
