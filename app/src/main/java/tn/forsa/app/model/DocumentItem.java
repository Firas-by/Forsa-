package tn.forsa.app.model;

import java.io.Serializable;

/**
 * Role: Represents one required document inside an opportunity's checklist.
 * Each document tracks one of three statuses:
 * "Disponible", "Manquant", or "Seulement après présélection".
 */
public class DocumentItem implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String STATUS_AVAILABLE = "Disponible";
    public static final String STATUS_MISSING = "Manquant";
    public static final String STATUS_AFTER_PRESELECTION = "Seulement après présélection";

    private String name;
    private String status;

    public DocumentItem(String name, String status) {
        this.name = name;
        this.status = status;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public boolean isAvailable() {
        return STATUS_AVAILABLE.equals(status);
    }

    /**
     * Cycles the document status on tap in OpportunityDetailsActivity:
     * Manquant -> Disponible -> Seulement après présélection -> Manquant.
     */
    public void cycleStatus() {
        if (STATUS_MISSING.equals(status)) {
            status = STATUS_AVAILABLE;
        } else if (STATUS_AVAILABLE.equals(status)) {
            status = STATUS_AFTER_PRESELECTION;
        } else {
            status = STATUS_MISSING;
        }
    }
}
