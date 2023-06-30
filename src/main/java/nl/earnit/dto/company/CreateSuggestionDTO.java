package nl.earnit.dto.company;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Create suggestion.
 */
@XmlRootElement
public class CreateSuggestionDTO {
    private Integer suggestion;

    /**
     * Instantiates a new Create suggestion.
     */
    public CreateSuggestionDTO() {

    }

    /**
     * Instantiates a new Create suggestion.
     *
     * @param suggestion the suggestion
     */
    public CreateSuggestionDTO(Integer suggestion) {
        this.suggestion = suggestion;
    }

    /**
     * Gets suggestion.
     *
     * @return the suggestion
     */
    public Integer getSuggestion() {
        return suggestion;
    }

    /**
     * Sets suggestion.
     *
     * @param suggestion the suggestion
     */
    public void setSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }
}
