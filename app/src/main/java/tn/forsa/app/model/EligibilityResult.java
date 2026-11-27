package tn.forsa.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Role: Holds the output of EligibilityEngine.evaluate(Student, Opportunity).
 * Keeps formal eligibility criteria strictly separate from personal fit observations.
 */
public class EligibilityResult implements Serializable {

    private static final long serialVersionUID = 1L;

    public enum Status {
        ELIGIBLE,
        POTENTIALLY_ELIGIBLE,
        NOT_ELIGIBLE,
        INSUFFICIENT_INFO
    }

    private Status status;
    private ArrayList<String> reasons;
    private ArrayList<String> personalFitNotes;
    private int compatibilityPercent;

    public EligibilityResult() {
        this.status = Status.INSUFFICIENT_INFO;
        this.reasons = new ArrayList<>();
        this.personalFitNotes = new ArrayList<>();
        this.compatibilityPercent = 0;
    }

    public Status getStatus() {
        return status;
    }

    public void setStatus(Status status) {
        this.status = status;
    }

    public ArrayList<String> getReasons() {
        return reasons;
    }

    public void setReasons(ArrayList<String> reasons) {
        this.reasons = reasons != null ? reasons : new ArrayList<String>();
    }

    public ArrayList<String> getPersonalFitNotes() {
        return personalFitNotes;
    }

    public void setPersonalFitNotes(ArrayList<String> personalFitNotes) {
        this.personalFitNotes = personalFitNotes != null ? personalFitNotes : new ArrayList<String>();
    }

    public int getCompatibilityPercent() {
        return compatibilityPercent;
    }

    public void setCompatibilityPercent(int compatibilityPercent) {
        this.compatibilityPercent = compatibilityPercent;
    }
}
