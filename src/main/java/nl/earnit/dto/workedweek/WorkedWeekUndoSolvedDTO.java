package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkedWeekUndoSolvedDTO {
    private Boolean solved;

    public WorkedWeekUndoSolvedDTO() {}

    public WorkedWeekUndoSolvedDTO(Boolean solved) {
        this.solved = solved;
    }

    public Boolean getSolved() {
        return solved;
    }

    public void setSolved(Boolean solved) {
        this.solved = solved;
    }
}
