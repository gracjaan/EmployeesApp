package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Create suggestion.
 */
@XmlRootElement
public class CreateSuggestion {
    private Integer suggestion;

    /**
     * Instantiates a new Create suggestion.
     */
    public CreateSuggestion() {

    }

    /**
     * Instantiates a new Create suggestion.
     *
     * @param suggestion the suggestion
     */
    public CreateSuggestion(Integer suggestion) {
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
