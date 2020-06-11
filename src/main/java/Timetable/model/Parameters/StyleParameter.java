package Timetable.model.Parameters;

public class StyleParameter {
    private String paneStyle = null;
    private String labelStyle = null;

    public StyleParameter() {
    }

    public StyleParameter(String paneStyle) {
        this.paneStyle = paneStyle;
    }

    public StyleParameter(String paneStyle, String labelStyle) {
        this.paneStyle = paneStyle;
        this.labelStyle = labelStyle;
    }

    public String getPaneStyle() {
        return paneStyle;
    }

    public void setPaneStyle(String paneStyle) {
        this.paneStyle = paneStyle;
    }

    public String getLabelStyle() {
        return labelStyle;
    }

    public void setLabelStyle(String labelStyle) {
        this.labelStyle = labelStyle;
    }

    public boolean hasPaneStyle() {
        return (this.paneStyle != null);
    }

    public boolean hasLabelStyle() {
        return (this.labelStyle != null);
    }

}
