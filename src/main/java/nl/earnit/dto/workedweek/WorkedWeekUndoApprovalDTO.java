package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

/**
 * The type Worked week undo approval dto.
 */
@XmlRootElement
public class WorkedWeekUndoApprovalDTO {
    private Boolean approve;

    /**
     * Instantiates a new Worked week undo approval dto.
     */
    public WorkedWeekUndoApprovalDTO() {}

    /**
     * Instantiates a new Worked week undo approval dto.
     *
     * @param approve the approval
     */
    public WorkedWeekUndoApprovalDTO(Boolean approve) {
        this.approve = approve;
    }

    /**
     * Gets approve.
     *
     * @return the approval
     */
    public Boolean getApprove() {
        return approve;
    }

    /**
     * Sets approve.
     *
     * @param approve the approval
     */
    public void setApprove(Boolean approve) {
        this.approve = approve;
    }
}
