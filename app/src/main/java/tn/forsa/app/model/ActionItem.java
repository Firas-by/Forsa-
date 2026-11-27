package tn.forsa.app.model;

/**
 * Role: Represents one prioritized action in the student's "Tes 90 prochains jours" roadmap.
 */
public class ActionItem {

    public static final String TARGET_PASSPORT = "PASSPORT";
    public static final String TARGET_OPPORTUNITY = "OPPORTUNITY";
    public static final String TARGET_ADVISOR = "ADVISOR";
    public static final String TARGET_OPPORTUNITIES_LIST = "OPPORTUNITIES_LIST";

    private int priority; // 1 = High, 2 = Medium, 3 = Recommended
    private String title;
    private String description;
    private String timeframe;
    private String targetType;
    private int relatedOpportunityId;
    private boolean completed;

    public ActionItem(int priority, String title, String description,
                      String timeframe, String targetType, int relatedOpportunityId) {
        this.priority = priority;
        this.title = title;
        this.description = description;
        this.timeframe = timeframe;
        this.targetType = targetType;
        this.relatedOpportunityId = relatedOpportunityId;
        this.completed = false;
    }

    public int getPriority() {
        return priority;
    }

    public String getTitle() {
        return title;
    }

    public String getDescription() {
        return description;
    }

    public String getTimeframe() {
        return timeframe;
    }

    public String getTargetType() {
        return targetType;
    }

    public int getRelatedOpportunityId() {
        return relatedOpportunityId;
    }

    public boolean isCompleted() {
        return completed;
    }

    public void setCompleted(boolean completed) {
        this.completed = completed;
    }
}
