package nl.earnit.dto.company;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Create note.
 */
@XmlRootElement
public class CreateNoteDTO {
    private String note;

    /**
     * Instantiates a new Create note.
     */
    public CreateNoteDTO() {

    }

    /**
     * Instantiates a new Create note.
     *
     * @param note the note
     */
    public CreateNoteDTO(String note) {
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
