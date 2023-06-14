package nl.earnit.dto.workedweek;

import jakarta.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class WorkedWeekUndoApprovalDTO {
    private Boolean approve;

    public WorkedWeekUndoApprovalDTO() {}

    public WorkedWeekUndoApprovalDTO(Boolean approve) {
        this.approve = approve;
    }

    public Boolean getApprove() {
        return approve;
    }

    public void setApprove(Boolean approve) {
        this.approve = approve;
    }
}
