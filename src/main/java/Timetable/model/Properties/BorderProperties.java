package Timetable.model.Properties;

public class BorderProperties {
    private Boolean rightBorder = true;
    private Boolean leftBorder = true;

    public BorderProperties(Boolean rightBorder, Boolean leftBorder) {
        this.rightBorder = rightBorder;
        this.leftBorder = leftBorder;
    }

    public BorderProperties() {
    }

    public Boolean getRightBorder() {
        return rightBorder;
    }

    public void setRightBorder(Boolean rightBorder) {
        this.rightBorder = rightBorder;
    }

    public Boolean getLeftBorder() {
        return leftBorder;
    }

    public void setLeftBorder(Boolean leftBorder) {
        this.leftBorder = leftBorder;
    }
}
