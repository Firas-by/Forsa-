package tn.forsa.app.model;

/**
 * Role: Simple data holder representing one of the 4 guidance pathways in MainActivity's GridView.
 */
public class PathwayItem {

    private final String title;
    private final String subtitle;
    private final String categoryKey;
    private final int accentColorRes;

    public PathwayItem(String title, String subtitle, String categoryKey, int accentColorRes) {
        this.title = title;
        this.subtitle = subtitle;
        this.categoryKey = categoryKey;
        this.accentColorRes = accentColorRes;
    }

    public String getTitle() {
        return title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public String getCategoryKey() {
        return categoryKey;
    }

    public int getAccentColorRes() {
        return accentColorRes;
    }
}
