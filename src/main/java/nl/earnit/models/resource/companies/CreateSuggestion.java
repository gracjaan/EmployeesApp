package nl.earnit.models.resource.companies;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class CreateSuggestion {
    private Integer suggestion;

    public CreateSuggestion() {

    }

    public CreateSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }

    public Integer getSuggestion() {
        return suggestion;
    }

    public void setSuggestion(Integer suggestion) {
        this.suggestion = suggestion;
    }
}
