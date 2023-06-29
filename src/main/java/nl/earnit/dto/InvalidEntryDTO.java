package nl.earnit.dto;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Invalid entry.
 */
@XmlRootElement
public class InvalidEntryDTO {
    private String field;

    /**
     * Instantiates a new Invalid entry.
     */
    public InvalidEntryDTO() {}

    /**
     * Instantiates a new Invalid entry.
     *
     * @param field the field
     */
    public InvalidEntryDTO(String field) {
        this.field = field;
    }

    /**
     * Gets field.
     *
     * @return the field
     */
    public String getField() {
        return field;
    }

    /**
     * Sets field.
     *
     * @param field the field
     */
    public void setField(String field) {
        this.field = field;
    }
}
