package Timetable.model.Parameters;

public class StyleParameter {
    // TODO set @NonNull or @Nullable to each variable and it's getter according to it's properties in the table
    // TODO alter all setters to return new object instead of mutating existing one see BorderProperties for example

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
