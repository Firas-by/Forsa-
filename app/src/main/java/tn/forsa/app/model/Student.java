package tn.forsa.app.model;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Role: Represents the Tunisian Licence (BAC+3) student profile ("Student Passport")
 * used by the Wijhti eligibility engine and 90-day action plan builder.
 * Implements Serializable so the profile can be inspected or passed between Activities.
 */
public class Student implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final double UNSET_GRADE = -1.0;
    public static final int UNSET_AGE = -1;

    private String name;
    private String institution;
    private String specialization;
    private String exactDegreeTitle;
    private double bacAverage;
    private double l1Average;
    private double l2Average;
    private double l3Average; // Optional (-1.0 if not entered yet)
    private int ageOnSept2027;
    private int yearsRepeated;
    private boolean hadInterruption;
    private ArrayList<String> skills;
    private boolean hasDeployedProject;
    private boolean hasPfe;
    private String preferredDestination; // "Tunisie" or "Europe"
    private boolean needsFunding;
    private String studyLanguage;

    public Student() {
        this.skills = new ArrayList<>();
        this.bacAverage = UNSET_GRADE;
        this.l1Average = UNSET_GRADE;
        this.l2Average = UNSET_GRADE;
        this.l3Average = UNSET_GRADE;
        this.ageOnSept2027 = UNSET_AGE;
    }

    /**
     * Official Tunisian Ministry formula for the government scholarship (Circulaire 50/26):
     * MSD = (0.5 * Bac + L1 + L2) / 2.5
     * Returns UNSET_GRADE (-1.0) if any of Bac, L1, or L2 is missing.
     */
    public double getMsd() {
        if (bacAverage < 0 || l1Average < 0 || l2Average < 0) {
            return UNSET_GRADE;
        }
        return (0.5 * bacAverage + l1Average + l2Average) / 2.5;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getInstitution() {
        return institution;
    }

    public void setInstitution(String institution) {
        this.institution = institution;
    }

    public String getSpecialization() {
        return specialization;
    }

    public void setSpecialization(String specialization) {
        this.specialization = specialization;
    }

    public String getExactDegreeTitle() {
        return exactDegreeTitle;
    }

    public void setExactDegreeTitle(String exactDegreeTitle) {
        this.exactDegreeTitle = exactDegreeTitle;
    }

    public double getBacAverage() {
        return bacAverage;
    }

    public void setBacAverage(double bacAverage) {
        this.bacAverage = bacAverage;
    }

    public double getL1Average() {
        return l1Average;
    }

    public void setL1Average(double l1Average) {
        this.l1Average = l1Average;
    }

    public double getL2Average() {
        return l2Average;
    }

    public void setL2Average(double l2Average) {
        this.l2Average = l2Average;
    }

    public double getL3Average() {
        return l3Average;
    }

    public void setL3Average(double l3Average) {
        this.l3Average = l3Average;
    }

    public int getAgeOnSept2027() {
        return ageOnSept2027;
    }

    public void setAgeOnSept2027(int ageOnSept2027) {
        this.ageOnSept2027 = ageOnSept2027;
    }

    public int getYearsRepeated() {
        return yearsRepeated;
    }

    public void setYearsRepeated(int yearsRepeated) {
        this.yearsRepeated = yearsRepeated;
    }

    public boolean isHadInterruption() {
        return hadInterruption;
    }

    public void setHadInterruption(boolean hadInterruption) {
        this.hadInterruption = hadInterruption;
    }

    public ArrayList<String> getSkills() {
        return skills;
    }

    public void setSkills(ArrayList<String> skills) {
        this.skills = skills != null ? skills : new ArrayList<String>();
    }

    public boolean isHasDeployedProject() {
        return hasDeployedProject;
    }

    public void setHasDeployedProject(boolean hasDeployedProject) {
        this.hasDeployedProject = hasDeployedProject;
    }

    public boolean isHasPfe() {
        return hasPfe;
    }

    public void setHasPfe(boolean hasPfe) {
        this.hasPfe = hasPfe;
    }

    public String getPreferredDestination() {
        return preferredDestination;
    }

    public void setPreferredDestination(String preferredDestination) {
        this.preferredDestination = preferredDestination;
    }

    public boolean isNeedsFunding() {
        return needsFunding;
    }

    public void setNeedsFunding(boolean needsFunding) {
        this.needsFunding = needsFunding;
    }

    public String getStudyLanguage() {
        return studyLanguage;
    }

    public void setStudyLanguage(String studyLanguage) {
        this.studyLanguage = studyLanguage;
    }
}
