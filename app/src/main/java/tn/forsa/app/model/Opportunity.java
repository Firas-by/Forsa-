package tn.forsa.app.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Locale;

/**
 * Role: Represents a post-Licence opportunity (Master in Tunisia, scholarship abroad,
 * job/CIVP, or freelance path) with published eligibility rules and official source metadata.
 */
public class Opportunity implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final String CAT_MASTER_TN = "MASTER_TN";
    public static final String CAT_SCHOLARSHIP_ABROAD = "SCHOLARSHIP_ABROAD";
    public static final String CAT_JOB = "JOB";
    public static final String CAT_FREELANCE = "FREELANCE";

    private int id;
    private String title;
    private String institution;
    private String category;
    private ArrayList<String> acceptedDegrees;
    private double minMsd; // 0 = no MSD threshold
    private int maxAge;    // 0 = no age limit
    private boolean requiresNoRepeat;
    private boolean requiresInstitutionNomination;
    private boolean variableConsortiumRules; // True for generic Erasmus Mundus entry
    private String deadline; // Format: yyyy-MM-dd (empty if continuous)
    private ArrayList<String> requiredDocuments;
    private ArrayList<DocumentItem> documentItems;
    private String fundingInfo;
    private String officialSourceUrl;
    private String lastVerified;
    private String academicYear;
    private String notes;

    public Opportunity() {
        this.acceptedDegrees = new ArrayList<>();
        this.requiredDocuments = new ArrayList<>();
        this.documentItems = new ArrayList<>();
    }

    public Opportunity(int id, String title, String institution, String category,
                       ArrayList<String> acceptedDegrees, double minMsd, int maxAge,
                       boolean requiresNoRepeat, boolean requiresInstitutionNomination,
                       boolean variableConsortiumRules, String deadline,
                       ArrayList<String> requiredDocuments, String fundingInfo,
                       String officialSourceUrl, String lastVerified,
                       String academicYear, String notes) {
        this.id = id;
        this.title = title;
        this.institution = institution;
        this.category = category;
        this.acceptedDegrees = acceptedDegrees != null ? acceptedDegrees : new ArrayList<String>();
        this.minMsd = minMsd;
        this.maxAge = maxAge;
        this.requiresNoRepeat = requiresNoRepeat;
        this.requiresInstitutionNomination = requiresInstitutionNomination;
        this.variableConsortiumRules = variableConsortiumRules;
        this.deadline = deadline;
        this.fundingInfo = fundingInfo;
        this.officialSourceUrl = officialSourceUrl;
        this.lastVerified = lastVerified;
        this.academicYear = academicYear;
        this.notes = notes;
        setRequiredDocuments(requiredDocuments);
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public ArrayList<String> getAcceptedDegrees() {
        return acceptedDegrees;
    }

    public void setAcceptedDegrees(ArrayList<String> acceptedDegrees) {
        this.acceptedDegrees = acceptedDegrees != null ? acceptedDegrees : new ArrayList<String>();
    }

    public double getMinMsd() {
        return minMsd;
    }

    public void setMinMsd(double minMsd) {
        this.minMsd = minMsd;
    }

    public int getMaxAge() {
        return maxAge;
    }

    public void setMaxAge(int maxAge) {
        this.maxAge = maxAge;
    }

    public boolean isRequiresNoRepeat() {
        return requiresNoRepeat;
    }

    public void setRequiresNoRepeat(boolean requiresNoRepeat) {
        this.requiresNoRepeat = requiresNoRepeat;
    }

    public boolean isRequiresInstitutionNomination() {
        return requiresInstitutionNomination;
    }

    public void setRequiresInstitutionNomination(boolean requiresInstitutionNomination) {
        this.requiresInstitutionNomination = requiresInstitutionNomination;
    }

    public boolean isVariableConsortiumRules() {
        return variableConsortiumRules;
    }

    public void setVariableConsortiumRules(boolean variableConsortiumRules) {
        this.variableConsortiumRules = variableConsortiumRules;
    }

    public String getDeadline() {
        return deadline;
    }

    public void setDeadline(String deadline) {
        this.deadline = deadline;
    }

    public ArrayList<String> getRequiredDocuments() {
        return requiredDocuments;
    }

    public void setRequiredDocuments(ArrayList<String> requiredDocuments) {
        this.requiredDocuments = requiredDocuments != null ? requiredDocuments : new ArrayList<String>();
        this.documentItems = new ArrayList<>();
        for (int i = 0; i < this.requiredDocuments.size(); i++) {
            String docName = this.requiredDocuments.get(i);
            String initialStatus = docName.toLowerCase(Locale.ROOT).contains("présélection")
                    ? DocumentItem.STATUS_AFTER_PRESELECTION
                    : DocumentItem.STATUS_MISSING;
            this.documentItems.add(new DocumentItem(docName, initialStatus));
        }
    }

    public ArrayList<DocumentItem> getDocumentItems() {
        return documentItems;
    }

    public String getFundingInfo() {
        return fundingInfo;
    }

    public void setFundingInfo(String fundingInfo) {
        this.fundingInfo = fundingInfo;
    }

    public String getOfficialSourceUrl() {
        return officialSourceUrl;
    }

    public void setOfficialSourceUrl(String officialSourceUrl) {
        this.officialSourceUrl = officialSourceUrl;
    }

    public String getLastVerified() {
        return lastVerified;
    }

    public void setLastVerified(String lastVerified) {
        this.lastVerified = lastVerified;
    }

    public String getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(String academicYear) {
        this.academicYear = academicYear;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Maps the opportunity category to the matching Guidance Office department name.
     */
    public String getMatchingAdvisorDepartment() {
        if (CAT_MASTER_TN.equals(category)) {
            return "Orientation Master";
        } else if (CAT_SCHOLARSHIP_ABROAD.equals(category)) {
            return "Bourses & Mobilité";
        } else if (CAT_JOB.equals(category)) {
            return "Emploi & Stage";
        } else {
            return "Entrepreneuriat";
        }
    }

    /**
     * Returns a user-friendly French category label.
     */
    public String getCategoryLabel() {
        if (CAT_MASTER_TN.equals(category)) {
            return "Master en Tunisie";
        } else if (CAT_SCHOLARSHIP_ABROAD.equals(category)) {
            return "Études à l'étranger";
        } else if (CAT_JOB.equals(category)) {
            return "Emploi";
        } else {
            return "Carrière indépendante";
        }
    }
}
