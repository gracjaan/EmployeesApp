package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Worked week undo solved dto.
 */
@XmlRootElement
public class WorkedWeekUndoSolvedDTO {
    private Boolean solved;

    /**
     * Instantiates a new Worked week undo solved dto.
     */
    public WorkedWeekUndoSolvedDTO() {}

    /**
     * Instantiates a new Worked week undo solved dto.
     *
     * @param solved the solved
     */
    public WorkedWeekUndoSolvedDTO(Boolean solved) {
        this.solved = solved;
    }

    /**
     * Gets solved.
     *
     * @return the solved
     */
    public Boolean getSolved() {
        return solved;
    }

    /**
     * Sets solved.
     *
     * @param solved the solved
     */
    public void setSolved(Boolean solved) {
        this.solved = solved;
    }
}
